package kr.co.samplepcb.xpse.resource;

import coolib.common.CCObjectResult;
import coolib.common.CCPagingResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.samplepcb.xpse.pojo.G5ShopOrderDetailDTO;
import kr.co.samplepcb.xpse.pojo.G5ShopOrderListDTO;
import kr.co.samplepcb.xpse.pojo.G5ShopOrderSearchParam;
import kr.co.samplepcb.xpse.security.JwtAuth;
import kr.co.samplepcb.xpse.service.G5ShopOrderService;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "G5 주문", description = "G5 주문 관리 API")
@SecurityRequirement(name = "Bearer Token")
@RestController
@RequestMapping("/api/g5ShopOrders")
public class G5ShopOrderResource {

    private final G5ShopOrderService g5ShopOrderService;

    public G5ShopOrderResource(G5ShopOrderService g5ShopOrderService) {
        this.g5ShopOrderService = g5ShopOrderService;
    }

    @Operation(summary = "주문 검색", description = "G5 주문 목록을 페이징 조건으로 검색합니다")
    @JwtAuth
    @GetMapping("/_search")
    public CCPagingResult<G5ShopOrderListDTO> search(Pageable pageable, G5ShopOrderSearchParam searchParam) {
        return this.g5ShopOrderService.search(pageable, searchParam);
    }

    @Operation(summary = "주문 상세 조회 (PK)", description = "주문 ID로 상세 정보를 조회합니다")
    @JwtAuth
    @GetMapping("/{odId}")
    public CCObjectResult<G5ShopOrderDetailDTO> getDetail(
            @Parameter(description = "주문 ID") @PathVariable Long odId) {
        return this.g5ShopOrderService.getDetail(odId);
    }

    @Operation(summary = "주문 상세 조회 (itId)", description = "아이템 ID로 주문 상세 정보를 조회합니다")
    @JwtAuth
    @GetMapping("/byItId/{itId}")
    public CCObjectResult<G5ShopOrderDetailDTO> getDetailByItId(
            @Parameter(description = "아이템 ID") @PathVariable String itId) {
        return this.g5ShopOrderService.getDetailByItId(itId);
    }
}
