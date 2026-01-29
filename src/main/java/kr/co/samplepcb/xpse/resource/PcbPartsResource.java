package kr.co.samplepcb.xpse.resource;

import coolib.common.CCResult;
import coolib.common.QueryParam;
import kr.co.samplepcb.xpse.pojo.PcbPartsSearchField;
import kr.co.samplepcb.xpse.pojo.PcbPartsSearchVM;
import kr.co.samplepcb.xpse.service.PcbPartsIC114Service;
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
    private final PcbPartsIC114Service pcbPartsIC114Service;
    private final DigikeySubService digikeySubService;

    public PcbPartsResource(PcbPartsService pcbPartsService, PcbPartsIC114Service pcbPartsIC114Service, DigikeySubService digikeySubService) {
        this.pcbPartsService = pcbPartsService;
        this.pcbPartsIC114Service = pcbPartsIC114Service;
        this.digikeySubService = digikeySubService;
    }

    @PostMapping(value = "/_uploadItemFileByEleparts")
    public CCResult uploadItemFileByEleparts(@RequestParam("file") MultipartFile file/*, HttpServletRequest request*/) {
        return this.pcbPartsService.indexAllByEleparts(file);
    }

    @PostMapping(value = "/_uploadItemFileByIC114")
    public CCResult uploadItemFileByIC114(@RequestParam("file") MultipartFile file/*, HttpServletRequest request*/) {
        return this.pcbPartsIC114Service.indexAllByIC114(file);
    }

    @PostMapping(value = "/_uploadItemFilesByIC114")
    public CCResult uploadItemFilesByIC114(@RequestParam("files") MultipartFile[] files/*, HttpServletRequest request*/) {
        return this.pcbPartsIC114Service.indexAllByIC114Multiple(files);
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

    @GetMapping("/_searchExactMatch")
    public CCResult searchExactMatch(@RequestParam String partName) {
        return this.pcbPartsService.searchExactMatch(partName);
    }

    @GetMapping("/_searchById")
    public CCResult searchById(@RequestParam String id) {
        return this.pcbPartsService.searchById(id);
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
