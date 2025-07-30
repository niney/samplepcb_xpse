package kr.co.samplepcb.xpse.resource;

import coolib.common.CCResult;
import coolib.common.QueryParam;
import kr.co.samplepcb.xpse.pojo.PcbPartsSearchField;
import kr.co.samplepcb.xpse.pojo.PcbPartsSearchVM;
import kr.co.samplepcb.xpse.service.PcbPartsService;
import kr.co.samplepcb.xpse.service.common.sub.DigikeySubService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/pcbParts")
public class PcbPartsResource {

    // service
    private final PcbPartsService pcbPartsService;
    private final DigikeySubService digikeySubService;

    public PcbPartsResource(PcbPartsService pcbPartsService, DigikeySubService digikeySubService) {
        this.pcbPartsService = pcbPartsService;
        this.digikeySubService = digikeySubService;
    }

    @PostMapping(value = "/_uploadItemFileByEleparts")
    public CCResult uploadItemFileByEleparts(@RequestParam("file") MultipartFile file/*, HttpServletRequest request*/) {
        this.pcbPartsService.indexAllByEleparts(file);
        return CCResult.ok();
    }

    @GetMapping("/_search")
    public CCResult search(@PageableDefault @SortDefault.SortDefaults({
            @SortDefault(sort = "_score", direction = Sort.Direction.DESC), // 높은 점수
            @SortDefault(sort = PcbPartsSearchField.INVENTORY_LEVEL, direction = Sort.Direction.DESC), // 재고 있음(많음)
            @SortDefault(sort = PcbPartsSearchField.PRICE1, direction = Sort.Direction.ASC) // 낮은 가격
    }) Pageable pageable, QueryParam queryParam, PcbPartsSearchVM pcbPartsSearchVM, String referencePrefix) {
        if (StringUtils.isNotEmpty(pcbPartsSearchVM.getToken())) {
            pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Direction.DESC, PcbPartsSearchField.WRITE_DATE));
        }
        return this.pcbPartsService.search(pageable, queryParam, pcbPartsSearchVM, referencePrefix);
    }

    @GetMapping("/_indexingByDigikey")
    public Mono<CCResult> indexingByDigikey(String partNumber) {
        CCResult ccResult = this.pcbPartsService.searchNonDigikeyParts(partNumber);
        if (ccResult.isResult()) {
            return Mono.just(CCResult.dataNotFound());
        }
        return this.digikeySubService.getProductDetails(partNumber)
                .flatMap(resultMap -> Mono.just(pcbPartsService.indexingByDigikey(partNumber, resultMap)));
    }

    @GetMapping("/_searchCandidateByDigikey")
    public Mono<CCResult> searchCandidateByDigikey(String partNumber, @RequestParam(required = false) String referencePrefix) {
        return this.digikeySubService.searchByKeyword(referencePrefix, partNumber, 2, 0)
                .flatMap(resultMap -> Mono.just(pcbPartsService.searchCandidateByDigikey(partNumber, referencePrefix, resultMap)));
    }

}
