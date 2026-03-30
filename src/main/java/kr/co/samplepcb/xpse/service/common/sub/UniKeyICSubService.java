package kr.co.samplepcb.xpse.service.common.sub;

import coolib.common.CCObjectResult;
import kr.co.samplepcb.xpse.config.ApplicationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

import static org.apache.logging.log4j.message.ParameterizedMessage.ERROR_PREFIX;

@Service
public class UniKeyICSubService {

    private static final Logger log = LoggerFactory.getLogger(UniKeyICSubService.class);

    private final WebClient webClient;

    public UniKeyICSubService(ApplicationProperties applicationProperties, WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl(applicationProperties.getUnikeyic().getBaseUrl())
                .defaultHeader("Authorization", applicationProperties.getUnikeyic().getApiKey())
                .build();
    }

    public Mono<CCObjectResult<Map<String, Object>>> searchByPartNumber(String partNumber) {
        Map<String, Object> request = Map.of("pro_sno", partNumber);

        return webClient
                .post()
                .uri("/search-v1/products/get-single-goods-usd")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
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
