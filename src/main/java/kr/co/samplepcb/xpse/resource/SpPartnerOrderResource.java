package kr.co.samplepcb.xpse.resource;

import coolib.common.CCObjectResult;
import coolib.common.CCResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.samplepcb.xpse.pojo.SpOrderSearchParam;
import kr.co.samplepcb.xpse.pojo.SpPartnerOrderItemCreateDTO;
import kr.co.samplepcb.xpse.security.JwtAuth;
import kr.co.samplepcb.xpse.service.SpOrderService;
import kr.co.samplepcb.xpse.service.SpPartnerOrderService;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "협력사 발주", description = "협력사 발주 관리 API")
@SecurityRequirement(name = "Bearer Token")
@RestController
@RequestMapping("/api/spPartnerOrders")
public class SpPartnerOrderResource {

    private final SpPartnerOrderService spPartnerOrderService;
    private final SpOrderService spOrderService;

    public SpPartnerOrderResource(SpPartnerOrderService spPartnerOrderService, SpOrderService spOrderService) {
        this.spPartnerOrderService = spPartnerOrderService;
        this.spOrderService = spOrderService;
    }

    @Operation(summary = "주문 검색", description = "주문 목록을 페이징 조건으로 검색합니다")
    @JwtAuth
    @GetMapping("/_search")
    public CCResult search(Pageable pageable, SpOrderSearchParam searchParam) {
        return this.spOrderService.search(pageable, searchParam);
    }

    @Operation(summary = "협력사 발주 다중 생성", description = "협력사 발주를 다중으로 일괄 생성합니다")
    @JwtAuth
    @PostMapping("/_batch")
    public CCObjectResult<List<SpPartnerOrderItemCreateDTO>> createBatch(@RequestBody List<SpPartnerOrderItemCreateDTO> createDTOs) {
        return this.spPartnerOrderService.createPartnerOrderBatch(createDTOs);
    }
}
