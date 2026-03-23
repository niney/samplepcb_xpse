package kr.co.samplepcb.xpse.resource;

import coolib.common.CCObjectResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.samplepcb.xpse.pojo.SpPartnerOrderItemCreateDTO;
import kr.co.samplepcb.xpse.security.JwtAuth;
import kr.co.samplepcb.xpse.service.SpPartnerOrderService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "협력사 발주", description = "협력사 발주 관리 API")
@SecurityRequirement(name = "Bearer Token")
@RestController
@RequestMapping("/api/spPartnerOrders")
public class SpPartnerOrderResource {

    private final SpPartnerOrderService spPartnerOrderService;

    public SpPartnerOrderResource(SpPartnerOrderService spPartnerOrderService) {
        this.spPartnerOrderService = spPartnerOrderService;
    }

    @Operation(summary = "협력사 발주 다중 생성", description = "협력사 발주를 다중으로 일괄 생성합니다")
    @JwtAuth
    @PostMapping("/_batch")
    public CCObjectResult<List<SpPartnerOrderItemCreateDTO>> createBatch(@RequestBody List<SpPartnerOrderItemCreateDTO> createDTOs) {
        return this.spPartnerOrderService.createPartnerOrderBatch(createDTOs);
    }
}
