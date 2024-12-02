package kr.co.samplepcb.xpse.resource;

import co.elastic.clients.transport.rest_client.RestClientTransport;
import coolib.common.CCObjectResult;
import coolib.common.CCResult;
import coolib.common.QueryParam;
import kr.co.samplepcb.xpse.pojo.PcbPartsSearchField;
import kr.co.samplepcb.xpse.pojo.PcbPartsSearchVM;
import kr.co.samplepcb.xpse.service.DigikeyService;
import kr.co.samplepcb.xpse.service.PcbPartsService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/api/pcbParts")
public class PcbPartsResource {

    // service
    private final PcbPartsService pcbPartsService;
    private final DigikeyService digikeyService;

    public PcbPartsResource(PcbPartsService pcbPartsService, DigikeyService digikeyService) {
        this.pcbPartsService = pcbPartsService;
        this.digikeyService = digikeyService;
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
    }) Pageable pageable, QueryParam queryParam, PcbPartsSearchVM pcbPartsSearchVM) {
        if (StringUtils.isNotEmpty(pcbPartsSearchVM.getToken())) {
            pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Direction.DESC, PcbPartsSearchField.WRITE_DATE));
        }
        return this.pcbPartsService.search(pageable, queryParam, pcbPartsSearchVM);
    }

    @GetMapping("/_indexingByDigikey")
    public Mono<CCResult> indexingByDigikey(String partNumber) {
        return this.digikeyService.getProductDetails(partNumber)
                .flatMap(resultMap -> Mono.just(pcbPartsService.indexingByDigikey(resultMap)));
    }

}
