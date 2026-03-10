package kr.co.samplepcb.xpse.resource;

import coolib.common.CCResult;
import kr.co.samplepcb.xpse.security.JwtAuth;
import kr.co.samplepcb.xpse.service.SpItemService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/spItems")
public class SpItemResource {

    private final SpItemService spItemService;

    public SpItemResource(SpItemService spItemService) {
        this.spItemService = spItemService;
    }

    @JwtAuth
    @GetMapping("/{itId}")
    public CCResult getDetail(@PathVariable String itId) {
        return this.spItemService.getDetail(itId);
    }
}
