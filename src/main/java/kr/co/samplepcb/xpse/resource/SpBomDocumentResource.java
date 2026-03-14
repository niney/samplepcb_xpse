package kr.co.samplepcb.xpse.resource;

import coolib.common.CCObjectResult;
import coolib.common.CCResult;
import coolib.common.CCPagingResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.samplepcb.xpse.pojo.SpBomDocumentCreateDTO;
import kr.co.samplepcb.xpse.pojo.SpBomDocumentSearchParam;
import kr.co.samplepcb.xpse.pojo.SpBomDocumentDetailDTO;
import kr.co.samplepcb.xpse.pojo.SpBomDocumentListDTO;
import kr.co.samplepcb.xpse.security.JwtAuth;
import kr.co.samplepcb.xpse.security.JwtUserPrincipal;
import kr.co.samplepcb.xpse.service.SpBomDocumentService;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "SP BOM", description = "SP BOM 문서 조회 API")
@SecurityRequirement(name = "Bearer Token")
@RestController
@RequestMapping("/api/spBomDocuments")
public class SpBomDocumentResource {

    private final SpBomDocumentService spBomDocumentService;

    public SpBomDocumentResource(SpBomDocumentService spBomDocumentService) {
        this.spBomDocumentService = spBomDocumentService;
    }

    @Operation(summary = "BOM 문서 검색", description = "회원의 BOM 문서를 페이징으로 검색합니다")
    @JwtAuth
    @GetMapping("/_search")
    public CCPagingResult<SpBomDocumentListDTO> search(Pageable pageable, SpBomDocumentSearchParam searchParam,
                           @AuthenticationPrincipal JwtUserPrincipal principal) {
        return this.spBomDocumentService.search(pageable, searchParam, principal.getSub());
    }

    @Operation(summary = "BOM 문서 단건 조회", description = "회원의 BOM 문서를 ID로 조회합니다")
    @JwtAuth
    @GetMapping("/{id}")
    public CCObjectResult<SpBomDocumentDetailDTO> getById(@Parameter(description = "문서 ID") @PathVariable Long id,
                                                 @AuthenticationPrincipal JwtUserPrincipal principal) {
        return this.spBomDocumentService.getById(id, principal.getSub());
    }

    @Operation(summary = "BOM 문서 저장", description = "회원의 BOM 문서를 저장하거나 중복 해시 기준으로 업데이트합니다")
    @JwtAuth
    @PostMapping
    public CCObjectResult<SpBomDocumentDetailDTO> save(@RequestBody SpBomDocumentCreateDTO dto,
                                                      @AuthenticationPrincipal JwtUserPrincipal principal) {
        return this.spBomDocumentService.save(dto, principal.getSub());
    }

    @Operation(summary = "BOM 문서 삭제", description = "회원의 BOM 문서를 ID로 삭제합니다")
    @JwtAuth
    @DeleteMapping("/{id}")
    public CCResult delete(@Parameter(description = "문서 ID") @PathVariable Long id,
                           @AuthenticationPrincipal JwtUserPrincipal principal) {
        return this.spBomDocumentService.delete(id, principal.getSub());
    }
}
