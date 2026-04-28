package kr.co.samplepcb.xpse.resource;

import coolib.common.CCObjectResult;
import coolib.common.CCResult;
import coolib.common.QueryParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.samplepcb.xpse.domain.document.PcbPartsSearch;
import kr.co.samplepcb.xpse.pojo.PcbPartsExternalBatchResult;
import kr.co.samplepcb.xpse.pojo.PcbPartsMultiSearchResult;
import kr.co.samplepcb.xpse.pojo.PcbPartsSearchField;
import kr.co.samplepcb.xpse.pojo.PcbPartsSearchVM;
import kr.co.samplepcb.xpse.service.PcbPartsIC114Service;
import kr.co.samplepcb.xpse.service.PcbPartsMultiSearchService;
import kr.co.samplepcb.xpse.service.PcbPartsService;
import kr.co.samplepcb.xpse.service.common.sub.DigikeySubService;
import kr.co.samplepcb.xpse.service.common.sub.UniKeyICSubService;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Tag(name = "PCB 부품", description = "PCB 부품 검색/업로드/인덱싱 API")
@RestController
@RequestMapping("/api/pcbParts")
public class PcbPartsResource {

    // service
    private final PcbPartsService pcbPartsService;
    private final PcbPartsIC114Service pcbPartsIC114Service;
    private final DigikeySubService digikeySubService;
    private final PcbPartsMultiSearchService pcbPartsMultiSearchService;
    private final UniKeyICSubService uniKeyICSubService;

    public PcbPartsResource(PcbPartsService pcbPartsService, PcbPartsIC114Service pcbPartsIC114Service, DigikeySubService digikeySubService, PcbPartsMultiSearchService pcbPartsMultiSearchService, UniKeyICSubService uniKeyICSubService) {
        this.pcbPartsService = pcbPartsService;
        this.pcbPartsIC114Service = pcbPartsIC114Service;
        this.digikeySubService = digikeySubService;
        this.pcbPartsMultiSearchService = pcbPartsMultiSearchService;
        this.uniKeyICSubService = uniKeyICSubService;
    }

    @Operation(summary = "Eleparts 파일 업로드", description = "Eleparts 형식의 부품 파일을 업로드하여 인덱싱합니다")
    @PostMapping(value = "/_uploadItemFileByEleparts")
    public CCResult uploadItemFileByEleparts(@RequestParam("file") MultipartFile file/*, HttpServletRequest request*/) {
        return this.pcbPartsService.indexAllByEleparts(file);
    }

    @Operation(summary = "단일 부품 저장", description = "PcbPartsSearch 객체를 받아 색인/업데이트합니다 (serviceType + partName 기준 upsert)")
    @PostMapping("/_savePart")
    public CCResult savePart(@RequestBody PcbPartsSearch part) {
        return this.pcbPartsService.savePart(part);
    }

    @Operation(summary = "다중 부품 저장", description = "여러 부품을 일괄 색인/업데이트합니다 (serviceType별 그룹 벌크 처리)")
    @PostMapping("/_saveParts")
    public CCResult saveParts(@RequestBody List<PcbPartsSearch> parts) {
        return this.pcbPartsService.saveParts(parts);
    }

    @Operation(summary = "IC114 파일 업로드", description = "IC114 형식의 부품 파일을 업로드하여 인덱싱합니다")
    @PostMapping(value = "/_uploadItemFileByIC114")
    public CCResult uploadItemFileByIC114(@RequestParam("file") MultipartFile file/*, HttpServletRequest request*/) {
        return this.pcbPartsIC114Service.indexAllByIC114(file);
    }

    @Operation(summary = "IC114 다중 파일 업로드", description = "IC114 형식의 부품 파일을 다중 업로드하여 인덱싱합니다")
    @PostMapping(value = "/_uploadItemFilesByIC114")
    public CCResult uploadItemFilesByIC114(@RequestParam("files") MultipartFile[] files/*, HttpServletRequest request*/) {
        return this.pcbPartsIC114Service.indexAllByIC114Multiple(files);
    }

    @Operation(summary = "부품 검색", description = "다양한 조건으로 PCB 부품을 검색합니다")
    @GetMapping("/_search")
    public CCResult search(@PageableDefault @SortDefault.SortDefaults({
            @SortDefault(sort = "_score", direction = Sort.Direction.DESC), // 높은 점수
            @SortDefault(sort = PcbPartsSearchField.PRICE, direction = Sort.Direction.ASC) // 낮은 가격
    }) Pageable pageable, QueryParam queryParam, PcbPartsSearchVM pcbPartsSearchVM, String referencePrefix) {
        if (StringUtils.isNotEmpty(pcbPartsSearchVM.getToken())) {
            pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Direction.DESC, PcbPartsSearchField.WRITE_DATE));
        }
        return this.pcbPartsService.search(pageable, queryParam, pcbPartsSearchVM, referencePrefix);
    }

    @Operation(summary = "부품 정확 매칭 검색", description = "부품명과 제조사명으로 정확히 일치하는 부품을 검색합니다")
    @GetMapping("/_searchExactMatch")
    public Mono<CCResult> searchExactMatch(
            @Parameter(description = "부품명") @RequestParam String partName,
            @Parameter(description = "제조사명") @RequestParam(required = false) String manufacturerName) {
        return this.pcbPartsService.searchExactMatch(partName, manufacturerName);
    }

    @Operation(summary = "부품 ID 검색", description = "부품 ID로 검색합니다")
    @GetMapping("/_searchById")
    public CCResult searchById(@Parameter(description = "부품 ID") @RequestParam String id) {
        return this.pcbPartsService.searchById(id);
    }

    @Operation(summary = "Digikey 인덱싱", description = "Digikey 부품번호로 부품 정보를 가져와 인덱싱합니다")
    @GetMapping("/_indexingByDigikey")
    public Mono<CCResult> indexingByDigikey(@Parameter(description = "Digikey 부품번호") String partNumber) {
        CCResult ccResult = this.pcbPartsService.searchNonDigikeyParts(partNumber);
        if (ccResult.isResult()) {
            return Mono.just(CCResult.dataNotFound());
        }
        return this.digikeySubService.getProductDetails(partNumber, null)
                .flatMap(resultMap -> Mono.just(pcbPartsService.indexingByDigikey(partNumber, resultMap)));
    }

    @Operation(summary = "Digikey 일괄 인덱싱", description = "여러 Digikey 부품번호를 일괄 인덱싱합니다")
    @PostMapping("/_indexingByDigikey")
    public Mono<CCResult> indexingByDigikeyMultiple(@RequestBody List<String> partNumbers) {
        return Flux.fromIterable(partNumbers)
                .flatMap(partNumber -> {
                    CCResult ccResult = this.pcbPartsService.searchNonDigikeyParts(partNumber);
                    if (ccResult.isResult()) {
                        return Mono.empty();
                    }
                    return this.digikeySubService.getProductDetails(partNumber, null)
                            .flatMap(resultMap -> Mono.just(pcbPartsService.indexingByDigikey(partNumber, resultMap)));
                }, 5)
                .collectList()
                .map(CCObjectResult::setSimpleData);
    }

    @Operation(summary = "Digikey 후보 검색", description = "Digikey에서 부품 후보를 검색합니다")
    @GetMapping("/_searchCandidateByDigikey")
    public Mono<CCResult> searchCandidateByDigikey(
            @Parameter(description = "부품번호") String partNumber,
            @Parameter(description = "참조 접두사") @RequestParam(required = false) String referencePrefix) {
        return this.digikeySubService.searchByKeyword(referencePrefix, partNumber, 2, 0)
                .flatMap(resultMap -> Mono.just(pcbPartsService.searchCandidateByDigikey(partNumber, referencePrefix, resultMap)));
    }

    @Operation(summary = "UniKeyIC 부품 검색 및 색인", description = "UniKeyIC API를 통해 부품번호로 검색하고 ES에 색인합니다")
    @GetMapping("/_searchByUniKeyIC")
    public Mono<CCResult> searchByUniKeyIC(
            @Parameter(description = "부품번호") @RequestParam String partNumber) {
        return this.uniKeyICSubService.searchByPartNumber(partNumber)
                .map(resultMap -> pcbPartsService.indexingByUniKeyIC(resultMap));
    }

    @Operation(summary = "다중 소스 검색", description = "여러 소스에서 부품을 통합 검색합니다")
    @GetMapping("/_searchMultiSource")
    public Mono<CCObjectResult<PcbPartsMultiSearchResult>> searchMultiSource(
            @Parameter(description = "검색어") @RequestParam String searchWord,
            @Parameter(description = "참조 접두사") @RequestParam(required = false) String referencePrefix) {
        return this.pcbPartsMultiSearchService.searchMultiSource(searchWord, referencePrefix);
    }

    @Operation(summary = "외부 공급사 일괄 검색 (Digikey + UniKeyIC 병렬)",
            description = "여러 partName 으로 Digikey/UniKeyIC 를 병렬 조회합니다. " +
                    "두 공급사 트랙은 병렬로 실행되며, 각 트랙 내부는 partName 입력 순서대로 순차 조회합니다 (ES 캐시 우선).")
    @PostMapping("/_searchExternalBatch")
    public Mono<CCObjectResult<PcbPartsExternalBatchResult>> searchExternalBatch(
            @RequestBody List<String> partNames) {
        return this.pcbPartsMultiSearchService.searchExternalBatch(partNames);
    }

    @Operation(summary = "다중 소스 순차 검색 (first-hit)",
            description = "자체 → 디지키 → UniKeyIC 순으로 순차 검색하며, 결과가 존재하는 첫 번째 소스를 반환합니다")
    @GetMapping("/_searchMultiSourceFirstHit")
    public Mono<CCObjectResult<PcbPartsMultiSearchResult>> searchMultiSourceFirstHit(
            @Parameter(description = "검색어") @RequestParam String searchWord,
            @Parameter(description = "참조 접두사") @RequestParam(required = false) String referencePrefix) {
        return this.pcbPartsMultiSearchService.searchMultiSourceFirstHit(searchWord, referencePrefix);
    }

}
