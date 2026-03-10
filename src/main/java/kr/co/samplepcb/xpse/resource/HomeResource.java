package kr.co.samplepcb.xpse.resource;

import kr.co.samplepcb.xpse.security.JwtUserPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HomeResource {

    @GetMapping("/")
    public String home() {
        return "running";
    }

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
