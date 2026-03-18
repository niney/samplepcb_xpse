package kr.co.samplepcb.xpse.resource;

import coolib.common.CCPagingResult;
import coolib.common.CCResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.samplepcb.xpse.pojo.SpEstimateListDTO;
import kr.co.samplepcb.xpse.pojo.SpEstimateSearchParam;
import kr.co.samplepcb.xpse.pojo.SpOrderSearchParam;
import kr.co.samplepcb.xpse.security.JwtAuth;
import kr.co.samplepcb.xpse.service.SpEstimateService;
import kr.co.samplepcb.xpse.service.SpOrderService;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@Tag(name = "SP 주문", description = "SP 주문 관리 API")
@SecurityRequirement(name = "Bearer Token")
@RestController
@RequestMapping("/api/spOrders")
public class SpOrderResource {

    private final SpOrderService spOrderService;
    private final SpEstimateService spEstimateService;

    public SpOrderResource(SpOrderService spOrderService, SpEstimateService spEstimateService) {
        this.spOrderService = spOrderService;
        this.spEstimateService = spEstimateService;
    }

    @Operation(summary = "주문 검색", description = "주문 목록을 페이징 조건으로 검색합니다")
    @JwtAuth
    @GetMapping("/_search")
    public CCResult search(Pageable pageable, SpOrderSearchParam searchParam) {
        return this.spOrderService.search(pageable, searchParam);
    }

    @Operation(summary = "견적서 + 협력사 견적서 검색", description = "견적서 목록을 협력사 견적서 하위 리스트와 함께 페이징 조건으로 검색합니다")
    @JwtAuth
    @GetMapping("/_searchWithEstimate")
    public CCPagingResult<SpEstimateListDTO> searchWithEstimate(Pageable pageable, SpEstimateSearchParam searchParam) {
        return this.spEstimateService.searchWithPartners(pageable, searchParam);
    }
}
