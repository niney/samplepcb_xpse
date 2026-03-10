package kr.co.samplepcb.xpse.resource;

import coolib.common.CCResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.samplepcb.xpse.pojo.SpOrderSearchParam;
import kr.co.samplepcb.xpse.security.JwtAuth;
import kr.co.samplepcb.xpse.service.SpOrderService;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@Tag(name = "SP 주문", description = "SP 주문 관리 API")
@SecurityRequirement(name = "Bearer Token")
@RestController
@RequestMapping("/api/spOrders")
public class SpOrderResource {

    private final SpOrderService spOrderService;

    public SpOrderResource(SpOrderService spOrderService) {
        this.spOrderService = spOrderService;
    }

    @Operation(summary = "주문 검색", description = "주문 목록을 페이징 조건으로 검색합니다")
    @JwtAuth
    @GetMapping("/_search")
    public CCResult search(Pageable pageable, SpOrderSearchParam searchParam) {
        return this.spOrderService.search(pageable, searchParam);
    }
}
