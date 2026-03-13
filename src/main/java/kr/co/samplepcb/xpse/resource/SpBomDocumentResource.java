package kr.co.samplepcb.xpse.resource;

import coolib.common.CCPagingResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.samplepcb.xpse.pojo.SpBomDocumentSearchParam;
import kr.co.samplepcb.xpse.pojo.SpBomDocumentListDTO;
import kr.co.samplepcb.xpse.security.JwtAuth;
import kr.co.samplepcb.xpse.security.JwtUserPrincipal;
import kr.co.samplepcb.xpse.service.SpBomDocumentService;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
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
}
