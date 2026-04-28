package kr.co.samplepcb.xpse.service;

import coolib.common.CCObjectResult;
import coolib.common.CCResult;
import kr.co.samplepcb.xpse.config.ApplicationProperties;
import kr.co.samplepcb.xpse.domain.document.PcbPartsSearch;
import kr.co.samplepcb.xpse.pojo.PcbPartsExternalBatchResult;
import kr.co.samplepcb.xpse.pojo.PcbPartsMultiSearchResult;
import kr.co.samplepcb.xpse.pojo.PcbPartsSearchField;
import kr.co.samplepcb.xpse.pojo.PcbPkgType;
import kr.co.samplepcb.xpse.repository.PcbPartsSearchRepository;
import kr.co.samplepcb.xpse.service.common.sub.DigikeyPartsParserSubService;
import kr.co.samplepcb.xpse.service.common.sub.DigikeySubService;
import kr.co.samplepcb.xpse.service.common.sub.UniKeyICPartsParserSubService;
import kr.co.samplepcb.xpse.service.common.sub.UniKeyICSubService;
import kr.co.samplepcb.xpse.util.CoolElasticUtils;
import kr.co.samplepcb.xpse.util.PcbPartsUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.HighlightQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.*;

@Service
public class PcbPartsMultiSearchService {

    private static final Logger log = LoggerFactory.getLogger(PcbPartsMultiSearchService.class);

    private final ElasticsearchOperations elasticsearchOperations;
    private final DigikeySubService digikeySubService;
    private final DigikeyPartsParserSubService digikeyPartsParserSubService;
    private final UniKeyICSubService uniKeyICSubService;
    private final UniKeyICPartsParserSubService uniKeyICPartsParserSubService;
    private final PcbPartsService pcbPartsService;
    private final PcbPartsSearchRepository pcbPartsSearchRepository;
    private final ApplicationProperties applicationProperties;

    public PcbPartsMultiSearchService(ElasticsearchOperations elasticsearchOperations,
                                      DigikeySubService digikeySubService,
                                      DigikeyPartsParserSubService digikeyPartsParserSubService,
                                      UniKeyICSubService uniKeyICSubService,
                                      UniKeyICPartsParserSubService uniKeyICPartsParserSubService,
                                      PcbPartsService pcbPartsService,
                                      PcbPartsSearchRepository pcbPartsSearchRepository,
                                      ApplicationProperties applicationProperties) {
        this.elasticsearchOperations = elasticsearchOperations;
        this.digikeySubService = digikeySubService;
        this.digikeyPartsParserSubService = digikeyPartsParserSubService;
        this.uniKeyICSubService = uniKeyICSubService;
        this.uniKeyICPartsParserSubService = uniKeyICPartsParserSubService;
        this.pcbPartsService = pcbPartsService;
        this.pcbPartsSearchRepository = pcbPartsSearchRepository;
        this.applicationProperties = applicationProperties;
    }

    /**
     * 자체(samplepcb) + 디지키(digikey) 양쪽 검색을 병렬로 수행합니다.
     *
     * <pre>
     * 검색 흐름 (Mono.zip 병렬 실행):
     *
     * [자체(samplepcb)]                        [디지키(digikey)]
     *   1. PART_NAME.keyword 완전일치            1. getProductDetails 완전일치
     *      → 결과 있으면 searchType="exact"         → 결과 있으면 searchType="exact"
     *   2. 없으면 → 파싱 ES 검색                  2. 없으면 → searchByKeyword
     *      → searchType="keyword"                  → searchType="keyword"
     *   3. 없으면 → part name 일반 텍스트 검색
     *      → searchType="keyword"
     *
     * → 병렬 완료 후 합쳐서 응답
     * </pre>
     *
     * @param searchWord      검색어 (필수)
     * @param referencePrefix 참조 접두사 (선택, 예: "R", "C", "L")
     * @return 소스별 검색 결과를 담은 CCResult
     */
    public Mono<CCObjectResult<PcbPartsMultiSearchResult>> searchMultiSource(String searchWord, String referencePrefix) {
        if (StringUtils.isEmpty(searchWord)) {
            CCObjectResult<PcbPartsMultiSearchResult> notFound = new CCObjectResult<>();
            notFound.setResult(false);
            notFound.setMessage("data not found");
            return Mono.just(notFound);
        }

        String safeReferencePrefix = StringUtils.defaultString(referencePrefix);

        // 자체(samplepcb) ES 검색 — 블로킹 호출이므로 Mono.fromCallable로 감싸서 실행
        Mono<PcbPartsMultiSearchResult.SourceResult> samplepcbMono = Mono.fromCallable(() ->
                searchSamplepcb(searchWord, safeReferencePrefix)
        );

        // 디지키(digikey) API 검색 — 논블로킹 WebClient 기반
        Mono<PcbPartsMultiSearchResult.SourceResult> digikeyMono =
                searchDigikey(searchWord, safeReferencePrefix);

        // UniKeyIC API 검색 — 논블로킹 WebClient 기반
        Mono<PcbPartsMultiSearchResult.SourceResult> unikeyicMono =
                searchUniKeyIC(searchWord);

        // 세 소스를 병렬로 실행하고 결과를 합쳐서 반환
        return Mono.zip(samplepcbMono, digikeyMono, unikeyicMono)
                .map(tuple -> {
                    PcbPartsMultiSearchResult result = new PcbPartsMultiSearchResult();
                    result.setSamplepcb(tuple.getT1());
                    result.setDigikey(tuple.getT2());
                    result.setUnikeyic(tuple.getT3());
                    CCObjectResult<PcbPartsMultiSearchResult> response = new CCObjectResult<>();
                    response.setResult(true);
                    response.setData(result);
                    return response;
                })
                .onErrorResume(e -> {
                    log.error("멀티 소스 검색 실패", e);
                    CCObjectResult<PcbPartsMultiSearchResult> error = new CCObjectResult<>();
                    error.setResult(false);
                    error.setMessage(e.getMessage());
                    return Mono.just(error);
                });
    }

    /**
     * 자체(samplepcb) → 디지키(digikey) → UniKeyIC 순으로 순차 검색하며,
     * 결과가 존재하는 첫 번째 소스를 반환합니다(조기 종료).
     *
     * <pre>
     * 검색 흐름 (순차 실행, first-hit 조기 종료):
     *
     * 1. 자체(samplepcb) ES 검색 → items 존재 시 samplepcb 필드에만 담아 반환
     * 2. 없으면 디지키 ES 캐시 → API 검색 → items 존재 시 digikey 필드에만 담아 반환
     * 3. 없으면 UniKeyIC ES 캐시 → API 검색 → unikeyic 필드에만 담아 반환
     * 4. 모두 비어 있으면 세 필드 모두 빈 SourceResult로 반환
     * </pre>
     *
     * 응답 구조는 {@link #searchMultiSource}와 동일하며, 히트한 소스만 데이터가 채워지고
     * 나머지 소스는 null로 남습니다.
     *
     * @param searchWord      검색어 (필수)
     * @param referencePrefix 참조 접두사 (선택)
     * @return 첫 번째 히트 소스의 결과를 담은 CCResult
     */
    public Mono<CCObjectResult<PcbPartsMultiSearchResult>> searchMultiSourceFirstHit(String searchWord, String referencePrefix) {
        if (StringUtils.isEmpty(searchWord)) {
            CCObjectResult<PcbPartsMultiSearchResult> notFound = new CCObjectResult<>();
            notFound.setResult(false);
            notFound.setMessage("data not found");
            return Mono.just(notFound);
        }

        String safeReferencePrefix = StringUtils.defaultString(referencePrefix);

        Mono<PcbPartsMultiSearchResult.SourceResult> samplepcbMono = Mono.fromCallable(() ->
                searchSamplepcb(searchWord, safeReferencePrefix)
        ).onErrorResume(e -> {
            log.warn("samplepcb 검색 실패, 다음 소스로 진행: {}", e.getMessage());
            return Mono.just(emptySourceResult());
        });

        return samplepcbMono
                .flatMap(samplepcbResult -> {
                    if (hasItems(samplepcbResult)) {
                        PcbPartsMultiSearchResult r = new PcbPartsMultiSearchResult();
                        r.setSamplepcb(samplepcbResult);
                        return Mono.just(r);
                    }
                    return searchDigikey(searchWord, safeReferencePrefix)
                            .onErrorResume(e -> {
                                log.warn("digikey 검색 실패, 다음 소스로 진행: {}", e.getMessage());
                                return Mono.just(emptySourceResult());
                            })
                            .flatMap(digikeyResult -> {
                                if (hasItems(digikeyResult)) {
                                    PcbPartsMultiSearchResult r = new PcbPartsMultiSearchResult();
                                    r.setDigikey(digikeyResult);
                                    return Mono.just(r);
                                }
                                return searchUniKeyIC(searchWord)
                                        .onErrorResume(e -> {
                                            log.warn("unikeyic 검색 실패, 빈 결과 반환: {}", e.getMessage());
                                            return Mono.just(emptySourceResult());
                                        })
                                        .map(unikeyicResult -> {
                                            PcbPartsMultiSearchResult r = new PcbPartsMultiSearchResult();
                                            r.setUnikeyic(unikeyicResult);
                                            return r;
                                        });
                            });
                })
                .map(result -> {
                    CCObjectResult<PcbPartsMultiSearchResult> response = new CCObjectResult<>();
                    response.setResult(true);
                    response.setData(result);
                    return response;
                })
                .onErrorResume(e -> {
                    log.error("멀티 소스 first-hit 검색 실패", e);
                    CCObjectResult<PcbPartsMultiSearchResult> error = new CCObjectResult<>();
                    error.setResult(false);
                    error.setMessage(e.getMessage());
                    return Mono.just(error);
                });
    }

    /**
     * 외부 공급사(Digikey + UniKeyIC) 일괄 검색.
     *
     * <pre>
     * 검색 흐름:
     *
     * [Digikey 트랙 (concatMap, partName 순차)]   [UniKeyIC 트랙 (concatMap, partName 순차)]
     *   PART_A → PART_B → PART_C                    PART_A → PART_B → PART_C
     *   각 단계: ES 캐시 → getProductDetails 완전일치  각 단계: ES 캐시 → searchByPartNumber 완전일치
     *           (키워드 폴백 없음)
     *
     * → 두 트랙은 Mono.zip 으로 병렬 실행
     * → 각 트랙 내부는 concatMap 으로 입력 순서 보존
     * → partName 단위 onErrorResume 으로 1개 실패가 트랙 전체 중단을 막음
     * → partName 이 정확한 부품번호라는 가정 하에 Digikey 키워드 검색은 수행하지 않음 (노이즈/추가 API 호출 회피)
     * </pre>
     *
     * @param partNames 조회할 부품명 리스트 (필수, 비어있을 수 없음)
     * @return Digikey/UniKeyIC 각 소스별 누적 items 를 담은 결과
     */
    public Mono<CCObjectResult<PcbPartsExternalBatchResult>> searchExternalBatch(List<String> partNames) {
        if (partNames == null || partNames.isEmpty()) {
            CCObjectResult<PcbPartsExternalBatchResult> notFound = new CCObjectResult<>();
            notFound.setResult(false);
            notFound.setMessage("data not found");
            return Mono.just(notFound);
        }

        Mono<PcbPartsMultiSearchResult.SourceResult> digikeyMono = Flux.fromIterable(partNames)
                .concatMap(name -> searchDigikeyExactOnly(name)
                        .onErrorResume(e -> {
                            log.warn("digikey batch 검색 실패 (partName={}): {}", name, e.getMessage());
                            return Mono.just(emptySourceResult());
                        }))
                .collectList()
                .map(this::mergeSourceResults);

        Mono<PcbPartsMultiSearchResult.SourceResult> unikeyicMono = Flux.fromIterable(partNames)
                .concatMap(name -> searchUniKeyIC(name)
                        .onErrorResume(e -> {
                            log.warn("unikeyic batch 검색 실패 (partName={}): {}", name, e.getMessage());
                            return Mono.just(emptySourceResult());
                        }))
                .collectList()
                .map(this::mergeSourceResults);

        return Mono.zip(digikeyMono, unikeyicMono)
                .map(tuple -> {
                    PcbPartsExternalBatchResult result = new PcbPartsExternalBatchResult();
                    result.setDigikey(tuple.getT1());
                    result.setUnikeyic(tuple.getT2());
                    CCObjectResult<PcbPartsExternalBatchResult> response = new CCObjectResult<>();
                    response.setResult(true);
                    response.setData(result);
                    return response;
                })
                .onErrorResume(e -> {
                    log.error("외부 공급사 일괄 검색 실패", e);
                    CCObjectResult<PcbPartsExternalBatchResult> error = new CCObjectResult<>();
                    error.setResult(false);
                    error.setMessage(e.getMessage());
                    return Mono.just(error);
                });
    }

    /**
     * partName 별 SourceResult 들을 입력 순서대로 단일 SourceResult 로 병합한다.
     * batch 응답에서는 partName 마다 exact/keyword 가 다를 수 있으므로 searchType 은 null.
     */
    private PcbPartsMultiSearchResult.SourceResult mergeSourceResults(List<PcbPartsMultiSearchResult.SourceResult> partResults) {
        List<Object> merged = new ArrayList<>();
        for (PcbPartsMultiSearchResult.SourceResult r : partResults) {
            if (r != null && r.getItems() != null) {
                merged.addAll(r.getItems());
            }
        }
        return new PcbPartsMultiSearchResult.SourceResult(null, merged);
    }

    private static boolean hasItems(PcbPartsMultiSearchResult.SourceResult r) {
        return r != null && r.getItems() != null && !r.getItems().isEmpty();
    }

    private static PcbPartsMultiSearchResult.SourceResult emptySourceResult() {
        return new PcbPartsMultiSearchResult.SourceResult(null, Collections.emptyList());
    }

/**
     * 자체(samplepcb) ES 검색: 완전일치 → 없으면 파싱 키워드 검색
     */
    private PcbPartsMultiSearchResult.SourceResult searchSamplepcb(String searchWord, String referencePrefix) {
        // samplepcb/eleparts serviceType만 조회
        Criteria serviceTypeCriteria = new Criteria(PcbPartsSearchField.SERVICE_TYPE)
                .in(PcbPkgType.SAMPLEPCB.getValue(), PcbPkgType.ELEPARTS.getValue());

        // 1. PART_NAME.keyword 완전일치
        Criteria exactCriteria = new Criteria(PcbPartsSearchField.PART_NAME + ".keyword").is(searchWord);
        HighlightQuery highlightQuery = CoolElasticUtils.createHighlightQuery(Set.of(PcbPartsSearchField.PART_NAME));
        Query exactQuery = new CriteriaQuery(new Criteria().and(serviceTypeCriteria).and(exactCriteria));
        exactQuery.setHighlightQuery(highlightQuery);

        SearchHits<PcbPartsSearch> exactHits = elasticsearchOperations.search(exactQuery, PcbPartsSearch.class);
        if (exactHits.hasSearchHits()) {
            List<?> items = CoolElasticUtils.getSourceWithHighlight(exactHits);
            return new PcbPartsMultiSearchResult.SourceResult("exact", items);
        }

        // 2. 파싱 ES 검색
        Map<String, List<String>> parsedKeywords = PcbPartsUtils.parseString(searchWord, referencePrefix);
        Criteria parsedCriteria = new Criteria().and(serviceTypeCriteria);
        Set<String> highlightFields = new HashSet<>();
        highlightFields.add(PcbPartsSearchField.PART_NAME);

        boolean hasConditions = buildParsedCriteria(parsedKeywords, parsedCriteria, highlightFields);
        if (hasConditions) {
            Query parsedQuery = new CriteriaQuery(parsedCriteria);
            parsedQuery.setHighlightQuery(CoolElasticUtils.createHighlightQuery(highlightFields));

            SearchHits<PcbPartsSearch> parsedHits = elasticsearchOperations.search(parsedQuery, PcbPartsSearch.class);
            if (parsedHits.hasSearchHits()) {
                List<?> items = CoolElasticUtils.getSourceWithHighlight(parsedHits);
                return new PcbPartsMultiSearchResult.SourceResult("keyword", items);
            }
        }

        // 3. part name 일반 텍스트 검색
        Criteria textCriteria = new Criteria(PcbPartsSearchField.PART_NAME).is(searchWord);
        Query textQuery = new CriteriaQuery(new Criteria().and(serviceTypeCriteria).and(textCriteria));
        textQuery.setHighlightQuery(highlightQuery);

        SearchHits<PcbPartsSearch> textHits = elasticsearchOperations.search(textQuery, PcbPartsSearch.class);
        List<?> items = CoolElasticUtils.getSourceWithHighlight(textHits);
        return new PcbPartsMultiSearchResult.SourceResult("keyword", items);
    }

    /**
     * 디지키 검색: ES 캐시 확인 → getProductDetails 완전일치 → 없으면 searchByKeyword
     */
    private Mono<PcbPartsMultiSearchResult.SourceResult> searchDigikey(String searchWord, String referencePrefix) {
        // ES 캐시 확인 (TTL 이내 색인된 데이터가 있으면 API 호출 생략)
        return Mono.fromCallable(() -> findFreshCachedResults(PcbPkgType.DIGIKEY.getValue(), searchWord))
                .flatMap(cached -> {
                    if (!cached.isEmpty()) {
                        log.debug("디지키 ES 캐시 히트: {}", searchWord);
                        return Mono.just(new PcbPartsMultiSearchResult.SourceResult("exact", cached));
                    }
                    // 캐시 미스 → getProductDetails 완전일치
                    return digikeySubService.getProductDetails(searchWord, null)
                            .flatMap(response -> {
                                if (response.isResult() && response.getData() != null) {
                                    CCObjectResult<PcbPartsSearch> parsed = digikeyPartsParserSubService.parseProduct(response.getData());
                                    if (parsed.isResult() && parsed.getData() != null) {
                                        List<PcbPartsSearch> items = pcbPartsService.indexExternalResults(
                                                Collections.singletonList(parsed.getData()));
                                        return Mono.just(new PcbPartsMultiSearchResult.SourceResult("exact", items));
                                    }
                                }
                                return searchDigikeyByKeyword(searchWord, referencePrefix);
                            })
                            .onErrorResume(e -> {
                                log.warn("디지키 ProductDetails 검색 실패, keyword 검색으로 폴백: {}", e.getMessage());
                                return searchDigikeyByKeyword(searchWord, referencePrefix);
                            });
                });
    }

    /**
     * 디지키 완전일치 전용 검색: ES 캐시 → getProductDetails (키워드 폴백 없음).
     * partName 이 정확한 부품번호인 batch 호출에 사용된다. 키워드 검색의 노이즈/오탐 및 추가 API 호출을 제거한다.
     */
    private Mono<PcbPartsMultiSearchResult.SourceResult> searchDigikeyExactOnly(String searchWord) {
        return Mono.fromCallable(() -> findFreshCachedResults(PcbPkgType.DIGIKEY.getValue(), searchWord))
                .flatMap(cached -> {
                    if (!cached.isEmpty()) {
                        log.debug("디지키 ES 캐시 히트 (exactOnly): {}", searchWord);
                        return Mono.just(new PcbPartsMultiSearchResult.SourceResult("exact", cached));
                    }
                    return digikeySubService.getProductDetails(searchWord, null)
                            .flatMap(response -> {
                                if (response.isResult() && response.getData() != null) {
                                    CCObjectResult<PcbPartsSearch> parsed = digikeyPartsParserSubService.parseProduct(response.getData());
                                    if (parsed.isResult() && parsed.getData() != null) {
                                        List<PcbPartsSearch> items = pcbPartsService.indexExternalResults(
                                                Collections.singletonList(parsed.getData()));
                                        return Mono.just(new PcbPartsMultiSearchResult.SourceResult("exact", items));
                                    }
                                }
                                return Mono.just(new PcbPartsMultiSearchResult.SourceResult("exact", Collections.emptyList()));
                            })
                            .onErrorResume(e -> {
                                log.warn("디지키 ProductDetails 검색 실패 (exactOnly, partName={}): {}", searchWord, e.getMessage());
                                return Mono.just(new PcbPartsMultiSearchResult.SourceResult("exact", Collections.emptyList()));
                            });
                });
    }

    /**
     * 디지키 키워드 검색
     */
    private Mono<PcbPartsMultiSearchResult.SourceResult> searchDigikeyByKeyword(String searchWord, String referencePrefix) {
        return digikeySubService.searchByKeyword(referencePrefix, searchWord, 10, 0)
                .map(response -> {
                    if (response.isResult() && response.getData() != null) {
                        List<PcbPartsSearch> products = digikeyPartsParserSubService.parseAllProducts(response.getData());
                        products = pcbPartsService.indexExternalResults(products);
                        return new PcbPartsMultiSearchResult.SourceResult("keyword", products);
                    }
                    return new PcbPartsMultiSearchResult.SourceResult("keyword", Collections.emptyList());
                })
                .onErrorReturn(new PcbPartsMultiSearchResult.SourceResult("keyword", Collections.emptyList()));
    }

    /**
     * UniKeyIC 검색: ES 캐시 확인 → 부품번호 정확매칭
     */
    private Mono<PcbPartsMultiSearchResult.SourceResult> searchUniKeyIC(String searchWord) {
        // ES 캐시 확인 (TTL 이내 색인된 데이터가 있으면 API 호출 생략)
        return Mono.fromCallable(() -> findFreshCachedResults(PcbPkgType.UNIKEYIC.getValue(), searchWord))
                .flatMap(cached -> {
                    if (!cached.isEmpty()) {
                        log.debug("UniKeyIC ES 캐시 히트: {}", searchWord);
                        return Mono.just(new PcbPartsMultiSearchResult.SourceResult("exact", cached));
                    }
                    // 캐시 미스 → 외부 API 호출
                    return uniKeyICSubService.searchByPartNumber(searchWord)
                            .map(response -> {
                                if (response.isResult() && response.getData() != null) {
                                    List<PcbPartsSearch> products = uniKeyICPartsParserSubService.parseProducts(response.getData());
                                    if (!products.isEmpty()) {
                                        products = pcbPartsService.indexExternalResults(products);
                                        return new PcbPartsMultiSearchResult.SourceResult("exact", products);
                                    }
                                }
                                return new PcbPartsMultiSearchResult.SourceResult("exact", Collections.emptyList());
                            })
                            .onErrorReturn(new PcbPartsMultiSearchResult.SourceResult("exact", Collections.emptyList()));
                });
    }

    /**
     * @param serviceType 서비스 유형 (e.g., "digikey", "unikeyic")
     * @param searchWord  검색어 (부품명)
     * @return 캐시 히트 (TTL 유효) 시 결과, 캐시 미스 시 빈 리스트
     */
    private List<PcbPartsSearch> findFreshCachedResults(String serviceType, String searchWord) {
        List<PcbPartsSearch> cached = pcbPartsSearchRepository
                .findByServiceTypeAndPartNameKeywordIn(serviceType, List.of(searchWord));

        if (cached.isEmpty()) {
            return Collections.emptyList();
        }

        long ttlMillis = Duration.ofHours(applicationProperties.getExternalCache().getTtlHours()).toMillis();
        long now = System.currentTimeMillis();

        boolean allFresh = cached.stream()
                .allMatch(part -> part.getLastModifiedDate() != null
                        && (now - part.getLastModifiedDate().getTime()) < ttlMillis);

        return allFresh ? cached : Collections.emptyList();
    }

    /**
     * 파싱된 키워드로 ES 검색 조건 구축
     */
    private boolean buildParsedCriteria(Map<String, List<String>> parsedKeywords, Criteria criteria, Set<String> highlightFields) {
        boolean hasConditions = false;
        for (Map.Entry<String, List<String>> entry : parsedKeywords.entrySet()) {
            String fieldName = entry.getKey();
            List<String> fieldValues = entry.getValue();
            if (fieldValues.isEmpty()) continue;

            String keywords = String.join(" ", fieldValues);
            List<String> keywordFieldList = getKeywordFieldList(fieldName);
            if (keywordFieldList != null) {
                addOrSubCriteria(keywordFieldList, keywords, criteria, highlightFields);
                hasConditions = true;
            }
        }
        return hasConditions;
    }

    /**
     * 필드명에 대응하는 keyword 필드 목록 반환
     */
    private List<String> getKeywordFieldList(String fieldName) {
        return switch (fieldName) {
            case PcbPartsSearchField.WATT -> PcbPartsSearchField.WATT_KEYWORD_LIST;
            case PcbPartsSearchField.TOLERANCE -> PcbPartsSearchField.TOLERANCE_KEYWORD_LIST;
            case PcbPartsSearchField.OHM -> PcbPartsSearchField.OHM_KEYWORD_LIST;
            case PcbPartsSearchField.CONDENSER -> PcbPartsSearchField.CONDENSER_KEYWORD_LIST;
            case PcbPartsSearchField.VOLTAGE -> PcbPartsSearchField.VOLTAGE_KEYWORD_LIST;
            case PcbPartsSearchField.CURRENT -> PcbPartsSearchField.CURRENT_KEYWORD_LIST;
            case PcbPartsSearchField.INDUCTOR -> PcbPartsSearchField.INDUCTOR_KEYWORD_LIST;
            default -> null;
        };
    }

    private static void addOrSubCriteria(List<String> keywordFieldNameList, String keywords, Criteria refCriteria, Set<String> highlightFields) {
        Criteria subCriteria = new Criteria();
        for (String keyword : keywordFieldNameList) {
            subCriteria = subCriteria.or(keyword).is(keywords);
            highlightFields.add(keyword);
        }
        refCriteria.subCriteria(subCriteria);
    }
}
