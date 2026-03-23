package kr.co.samplepcb.xpse.resource;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@Tag(name = "협력사 발주", description = "협력사 발주 관리 API")
@SecurityRequirement(name = "Bearer Token")
@RestController
@RequestMapping("/api/spPartnerOrders")
public class SpPartnerOrderResource {

}
