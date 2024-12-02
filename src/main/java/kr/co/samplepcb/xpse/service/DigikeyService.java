package kr.co.samplepcb.xpse.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import coolib.common.CCObjectResult;
import jakarta.annotation.PostConstruct;
import kr.co.samplepcb.xpse.config.ApplicationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Map;

import static kr.co.samplepcb.xpse.config.CacheConfig.PRODUCT_DETAILS;
import static kr.co.samplepcb.xpse.config.CacheConfig.SEARCH_RESULTS;
import static org.apache.logging.log4j.message.ParameterizedMessage.ERROR_PREFIX;

@Service
public class DigikeyService {

    private static final Logger log = LoggerFactory.getLogger(DigikeyService.class);

    public static class TokenResponse {
        @JsonProperty("access_token")
        private String accessToken;

        @JsonProperty("expires_in")
        private Long expiresIn;

        public String getAccessToken() {
            return accessToken;
        }

        public void setAccessToken(String accessToken) {
            this.accessToken = accessToken;
        }

        public Long getExpiresIn() {
            return expiresIn;
        }

        public void setExpiresIn(Long expiresIn) {
            this.expiresIn = expiresIn;
        }
    }

    public static class TokenInfo implements Serializable {
        private String accessToken;
        private LocalDateTime expiryTime;

        public String getAccessToken() {
            return accessToken;
        }

        public void setAccessToken(String accessToken) {
            this.accessToken = accessToken;
        }

        public LocalDateTime getExpiryTime() {
            return expiryTime;
        }

        public void setExpiryTime(LocalDateTime expiryTime) {
            this.expiryTime = expiryTime;
        }

        public boolean isValid() {
            return expiryTime != null && LocalDateTime.now().isBefore(expiryTime);
        }
    }

    private final ApplicationProperties applicationProperties;
    private final WebClient.Builder webClientBuilder;

    private String accessToken;
    private LocalDateTime tokenExpiry;

    private static final String TOKEN_FOLDER = "digikey";
    private static final String TOKEN_FILE = "token.json";
    private final ObjectMapper objectMapper;

    @Value("${app.storage.path:${user.home}/.xpse}")
    private String baseStoragePath;

    private Path getTokenPath() {
        return Paths.get(baseStoragePath, TOKEN_FOLDER, TOKEN_FILE);
    }

    public DigikeyService(ApplicationProperties applicationProperties, WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
        this.applicationProperties = applicationProperties;
        this.webClientBuilder = webClientBuilder;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void init() {
        try {
            // 기본 저장 경로 생성
            Path storagePath = Paths.get(baseStoragePath, TOKEN_FOLDER);
            Files.createDirectories(storagePath);
            log.info("Storage directory created at: {}", storagePath);
        } catch (IOException e) {
            log.error("Failed to create storage directory", e);
            throw new RuntimeException("Could not initialize storage", e);
        }

        webClientBuilder
                .baseUrl(applicationProperties.getDigikey().getBaseUrl())
                .defaultHeader("X-DIGIKEY-Client-Id", applicationProperties.getDigikey().getClientId())
                .filter((request, next) -> {
                    if (!request.url().getPath().contains("/oauth2/token")) {
                        return getValidToken()
                                .flatMap(token -> {
                                    HttpHeaders headers = new HttpHeaders();
                                    headers.addAll(request.headers());
                                    headers.setBearerAuth(token);

                                    // Create a mutable request with updated headers
                                    ClientRequest modifiedRequest = ClientRequest.from(request)
                                            .headers(httpHeaders -> httpHeaders.addAll(headers))
                                            .build();

                                    return next.exchange(modifiedRequest);
                                });
                    }
                    return next.exchange(request);
                });
    }

    private String getClientCredentials() {
        String credentials = applicationProperties.getDigikey().getClientId() + ":" + applicationProperties.getDigikey().getClientSecret();
        return Base64.getEncoder().encodeToString(credentials.getBytes());
    }

    private Mono<String> getNewToken() {
        return webClientBuilder.build()
                .post()
                .uri("/v1/oauth2/token")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Basic " + getClientCredentials())
                .body(BodyInserters.fromFormData("grant_type", "client_credentials"))
                .retrieve()
                .bodyToMono(TokenResponse.class)
                .map(response -> {
                    TokenInfo tokenInfo = new TokenInfo();
                    tokenInfo.setAccessToken(response.getAccessToken());
                    tokenInfo.setExpiryTime(LocalDateTime.now().plusSeconds(response.getExpiresIn()));

                    try {
                        objectMapper.writeValue(getTokenPath().toFile(), tokenInfo);
                        log.debug("Token saved to file successfully");
                    } catch (IOException e) {
                        log.error("Failed to save token to file: {}", e.getMessage());
                    }

                    return response.getAccessToken();
                });
    }

    private Mono<String> getValidToken() {
        Path tokenPath = getTokenPath();
        try {
            if (Files.exists(tokenPath)) {
                TokenInfo tokenInfo = objectMapper.readValue(tokenPath.toFile(), TokenInfo.class);
                if (tokenInfo.isValid()) {
                    log.debug("Using token from file cache");
                    return Mono.just(tokenInfo.getAccessToken());
                }
            }
        } catch (IOException e) {
            log.warn("Failed to read token file: {}", e.getMessage());
        }
        return getNewToken();
    }

    @Cacheable(value = SEARCH_RESULTS, key = "#keyword + '_' + #limit + '_' + #offset")
    public Mono<CCObjectResult<Map<String, Object>>> searchByKeyword(String keyword, int limit, int offset) {
        Map<String, Object> request = Map.of(
                "Keywords", keyword,
                "Limit", limit,
                "Offset", offset
        );

        return webClientBuilder.build()
                .post()
                .uri("/products/v4/search/keyword")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatusCode::isError, response ->
                        response.bodyToMono(String.class)
                                .flatMap(error -> Mono.error(
                                        new RuntimeException("Search failed: " + error))))
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .map(this::createSuccessResult)
                .onErrorResume(Exception.class, this::createFailureResult);
    }

    @Cacheable(value = PRODUCT_DETAILS, key = "#partNumber")
    public Mono<CCObjectResult<Map<String, Object>>> getProductDetails(String partNumber) {
        return webClientBuilder.build()
                .get()
                .uri("/products/v4/search/{partNumber}/productdetails", partNumber)
                .header("X-DIGIKEY-Locale-Currency", "KRW")
                .header("X-DIGIKEY-Locale-Site", "KR")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .map(this::createSuccessResult)
                .onErrorResume(Exception.class, this::createFailureResult);
    }

    private CCObjectResult<Map<String, Object>> createSuccessResult(Map<String, Object> dataMap) {
        CCObjectResult<Map<String, Object>> result = new CCObjectResult<>();
        result.setResult(true);
        result.setData(dataMap);
        return result;
    }

    private Mono<CCObjectResult<Map<String, Object>>> createFailureResult(Exception e) {
        log.error("{}{}", ERROR_PREFIX, e.getMessage());
        CCObjectResult<Map<String, Object>> result = new CCObjectResult<>();
        result.setResult(false);
        result.setMessage(e.getMessage());
        return Mono.just(result);
    }
}
