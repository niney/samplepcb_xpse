package kr.co.samplepcb.xpse.resource;

import coolib.common.CCResult;
import kr.co.samplepcb.xpse.pojo.SpOrderSearchParam;
import kr.co.samplepcb.xpse.security.JwtAuth;
import kr.co.samplepcb.xpse.service.SpOrderService;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/spOrders")
public class SpOrderResource {

    private final SpOrderService spOrderService;

    public SpOrderResource(SpOrderService spOrderService) {
        this.spOrderService = spOrderService;
    }

    @JwtAuth
    @GetMapping("/_search")
    public CCResult search(Pageable pageable, SpOrderSearchParam searchParam) {
        return this.spOrderService.search(pageable, searchParam);
    }
}
