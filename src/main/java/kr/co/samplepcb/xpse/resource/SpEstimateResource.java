package kr.co.samplepcb.xpse.resource;

import coolib.common.CCResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import kr.co.samplepcb.xpse.pojo.SpEstimateCreateDTO;
import kr.co.samplepcb.xpse.security.JwtAuth;
import kr.co.samplepcb.xpse.security.JwtUserPrincipal;
import kr.co.samplepcb.xpse.service.SpEstimateService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "SP 견적", description = "SP 견적서 API")
@SecurityRequirement(name = "Bearer Token")
@RestController
@RequestMapping("/api/spEstimates")
public class SpEstimateResource {

    private final SpEstimateService spEstimateService;

    public SpEstimateResource(SpEstimateService spEstimateService) {
        this.spEstimateService = spEstimateService;
    }

    @Operation(summary = "견적서 생성", description = "상품 + 장바구니 + 견적서 + 견적항목을 일괄 생성합니다")
    @JwtAuth
    @PostMapping
    public CCResult create(@RequestBody SpEstimateCreateDTO createDTO,
                           @AuthenticationPrincipal JwtUserPrincipal principal,
                           HttpServletRequest request) {
        return this.spEstimateService.create(createDTO, principal.getSub(), request.getRemoteAddr());
    }
}
