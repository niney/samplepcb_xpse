package kr.co.samplepcb.xpse.resource;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.samplepcb.xpse.security.JwtUserPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Tag(name = "Home", description = "헬스체크 및 인증 테스트")
@RestController
public class HomeResource {

    @Operation(summary = "헬스체크", description = "서버 동작 상태를 확인합니다")
    @GetMapping("/")
    public String home() {
        return "running";
    }

    @Operation(summary = "인증 테스트", description = "JWT 토큰 인증 상태를 확인합니다",
            security = @SecurityRequirement(name = "Bearer Token"))
    @GetMapping("/api/auth-test")
    public ResponseEntity<Map<String, Object>> authTest(@AuthenticationPrincipal JwtUserPrincipal principal) {
        return ResponseEntity.ok(Map.of(
                "sub", principal.getSub(),
                "mbName", principal.getMbName(),
                "mbLevel", principal.getMbLevel(),
                "mbNo", principal.getMbNo()
        ));
    }
}
