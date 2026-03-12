package kr.co.samplepcb.xpse.resource;

import coolib.common.CCResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import kr.co.samplepcb.xpse.pojo.SpItemCreateDTO;
import kr.co.samplepcb.xpse.pojo.SpItemUpdateDTO;
import kr.co.samplepcb.xpse.security.JwtAuth;
import kr.co.samplepcb.xpse.security.JwtUserPrincipal;
import kr.co.samplepcb.xpse.service.SpItemService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "SP 아이템", description = "SP 아이템 조회/수정 API")
@SecurityRequirement(name = "Bearer Token")
@RestController
@RequestMapping("/api/spItems")
public class SpItemResource {

    private final SpItemService spItemService;

    public SpItemResource(SpItemService spItemService) {
        this.spItemService = spItemService;
    }

    @Operation(summary = "아이템 생성", description = "상품을 생성하고 장바구니에 1:1로 등록합니다 (이미 존재하면 업데이트)")
    @JwtAuth
    @PostMapping
    public CCResult create(@RequestBody SpItemCreateDTO createDTO,
                           @AuthenticationPrincipal JwtUserPrincipal principal,
                           HttpServletRequest request) {
        return this.spItemService.create(createDTO, principal.getSub(), request.getRemoteAddr());
    }

    @Operation(summary = "아이템 상세 조회", description = "아이템 ID로 상세 정보를 조회합니다")
    @JwtAuth
    @GetMapping("/{itId}")
    public CCResult getDetail(@Parameter(description = "아이템 ID") @PathVariable String itId) {
        return this.spItemService.getDetail(itId);
    }

    @Operation(summary = "아이템 수정", description = "아이템 정보를 수정합니다")
    @JwtAuth
    @PostMapping("/{itId}")
    public CCResult update(@Parameter(description = "아이템 ID") @PathVariable String itId,
                           @RequestBody SpItemUpdateDTO updateDTO) {
        return this.spItemService.update(itId, updateDTO);
    }
}
