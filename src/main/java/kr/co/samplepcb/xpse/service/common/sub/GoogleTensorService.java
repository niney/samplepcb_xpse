package kr.co.samplepcb.xpse.service.common.sub;

import coolib.common.CCObjectResult;
import coolib.common.CCResult;
import kr.co.samplepcb.xpse.config.ApplicationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GoogleTensorService {

    private static final Logger log = LoggerFactory.getLogger(GoogleTensorService.class);

    private final String linServerUrl;

    public GoogleTensorService(ApplicationProperties applicationProperties) {
        this.linServerUrl = applicationProperties.getSpLinserver().getServerUrl();
    }

    public Mono<CCObjectResult<List<Double>>> getEncodedSentence(String sentence) {
        return WebClient
                .create(linServerUrl)
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/encode")
                        .queryParam("sentence", sentence)  // 단일 문장 쿼리 파라미터
                        .build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<CCObjectResult<List<Double>>>() {})  // 응답을 List<Double>로 받기
                .doOnError(error -> log.error(error.getMessage()));
    }

    public Mono<CCObjectResult<List<List<Double>>>> postEncodedSentences(List<String> sentences) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("sentences", sentences);  // JSON 본문에 sentences 추가

        return WebClient
                .create(linServerUrl)
                .post()
                .uri("/encode")
                .bodyValue(requestBody)  // 본문에 JSON 데이터 추가
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<CCObjectResult<List<List<Double>>>>() {})  // 응답을 List<List<Double>> 타입으로 받기
                .doOnError(WebClientResponseException.class, ex ->
                        log.error(ex.getResponseBodyAsString()));
    }

}
