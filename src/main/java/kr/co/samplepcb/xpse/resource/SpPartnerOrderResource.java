package kr.co.samplepcb.xpse.resource;

import coolib.common.CCResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.samplepcb.xpse.pojo.SpPartnerOrderCreateDTO;
import kr.co.samplepcb.xpse.pojo.SpPartnerOrderSearchParam;
import kr.co.samplepcb.xpse.security.JwtAuth;
import kr.co.samplepcb.xpse.service.SpPartnerOrderService;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "협력사 주문", description = "협력사 주문 관리 API")
@SecurityRequirement(name = "Bearer Token")
@RestController
@RequestMapping("/api/spPartnerOrders")
public class SpPartnerOrderResource {

    private final SpPartnerOrderService spPartnerOrderService;

    public SpPartnerOrderResource(SpPartnerOrderService spPartnerOrderService) {
        this.spPartnerOrderService = spPartnerOrderService;
    }

    @Operation(summary = "협력사 주문 검색", description = "협력사 주문 목록을 아이템/파트너/상태 조건으로 검색합니다")
    @JwtAuth
    @GetMapping("/_search")
    public CCResult search(Pageable pageable, SpPartnerOrderSearchParam searchParam) {
        return this.spPartnerOrderService.search(pageable, searchParam);
    }

    @Operation(summary = "협력사 주문 단건 생성", description = "협력사 주문을 단건으로 생성합니다")
    @JwtAuth
    @PostMapping
    public CCResult create(@RequestBody SpPartnerOrderCreateDTO createDTO) {
        return this.spPartnerOrderService.create(createDTO);
    }

    @Operation(summary = "협력사 주문 다중 생성", description = "협력사 주문을 다중으로 일괄 생성합니다")
    @JwtAuth
    @PostMapping("/_batch")
    public CCResult createBatch(@RequestBody List<SpPartnerOrderCreateDTO> createDTOs) {
        return this.spPartnerOrderService.createBatch(createDTOs);
    }
}
