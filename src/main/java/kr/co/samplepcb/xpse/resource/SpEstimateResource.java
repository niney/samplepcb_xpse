package kr.co.samplepcb.xpse.resource;

import coolib.common.CCObjectResult;
import coolib.common.CCPagingResult;
import coolib.common.CCResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import kr.co.samplepcb.xpse.domain.entity.SpPartnerEstimateItem;
import kr.co.samplepcb.xpse.pojo.SpEstimateCreateDTO;
import kr.co.samplepcb.xpse.pojo.SpEstimateDetailDTO;
import kr.co.samplepcb.xpse.pojo.SpEstimateListDTO;
import kr.co.samplepcb.xpse.pojo.SpEstimatePartnerSelectionDTO;
import kr.co.samplepcb.xpse.pojo.SpEstimateSearchParam;
import kr.co.samplepcb.xpse.pojo.SpPartnerEstimateItemCreateDTO;
import kr.co.samplepcb.xpse.security.JwtAuth;
import kr.co.samplepcb.xpse.security.JwtUserPrincipal;
import kr.co.samplepcb.xpse.service.SpEstimateService;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "SP 견적", description = "SP 견적서 API")
@SecurityRequirement(name = "Bearer Token")
@RestController
@RequestMapping("/api/spEstimates")
public class SpEstimateResource {

    private final SpEstimateService spEstimateService;

    public SpEstimateResource(SpEstimateService spEstimateService) {
        this.spEstimateService = spEstimateService;
    }

    @Operation(summary = "견적서 생성/수정", description = "상품 + 장바구니 + 견적서 + 견적항목을 일괄 생성(또는 upsert)합니다")
    @JwtAuth
    @PostMapping
    public CCObjectResult<SpEstimateDetailDTO> create(@RequestBody SpEstimateCreateDTO createDTO,
                           @AuthenticationPrincipal JwtUserPrincipal principal,
                           HttpServletRequest request) {
        return this.spEstimateService.create(createDTO, principal.getSub(), request.getRemoteAddr());
    }

    @Operation(summary = "견적서 상세 조회 (PK)",
            description = "견적서 ID로 상세 정보를 조회합니다. 각 견적 항목에 selected_partner_estimate_item_id가 함께 반환됩니다.")
    @JwtAuth
    @GetMapping("/{id}")
    public CCObjectResult<SpEstimateDetailDTO> getDetail(@Parameter(description = "견적서 ID") @PathVariable Long id) {
        return this.spEstimateService.getDetail(id);
    }

    @Operation(summary = "견적서 상세 조회 (itId)", description = "아이템 ID로 견적서 상세 정보를 조회합니다")
    @JwtAuth
    @GetMapping("/byItId/{itId}")
    public CCObjectResult<SpEstimateDetailDTO> getDetailByItId(@Parameter(description = "아이템 ID") @PathVariable String itId) {
        return this.spEstimateService.getDetailByItId(itId);
    }

    @Operation(summary = "견적서 목록 검색", description = "견적서 목록을 상태/아이템ID 조건으로 검색합니다")
    @JwtAuth
    @GetMapping("/_search")
    public CCPagingResult<SpEstimateListDTO> search(Pageable pageable, SpEstimateSearchParam searchParam) {
        return this.spEstimateService.search(pageable, searchParam);
    }

    @Operation(summary = "견적서 삭제", description = "견적서 및 관련 항목/파일을 모두 삭제합니다")
    @JwtAuth
    @DeleteMapping("/{id}")
    public CCResult delete(@Parameter(description = "견적서 ID") @PathVariable Long id) {
        return this.spEstimateService.delete(id);
    }

    @Operation(summary = "견적서 상태 변경", description = "견적서의 상태를 변경합니다")
    @JwtAuth
    @PostMapping("/{id}/status")
    public CCResult updateStatus(@Parameter(description = "견적서 ID") @PathVariable Long id,
                                 @RequestBody Map<String, String> body) {
        return this.spEstimateService.updateStatus(id, body.get("status"));
    }

    @Operation(summary = "협력사 견적 항목 등록/수정", description = "견적 항목에 대한 협력사 견적을 등록하거나 수정합니다")
    @JwtAuth
    @PostMapping("/{estimateItemId}/partnerEstimateItems")
    public CCObjectResult<SpPartnerEstimateItem> createPartnerEstimateItem(
            @Parameter(description = "견적 항목 ID") @PathVariable Long estimateItemId,
            @RequestBody SpPartnerEstimateItemCreateDTO createDTO) {
        return this.spEstimateService.createPartnerEstimateItem(estimateItemId, createDTO);
    }

    @Operation(summary = "협력사 견적 선택", description = "견적 항목에서 협력사 견적을 선택합니다")
    @JwtAuth
    @PostMapping("/items/{estimateItemId}/selectPartner")
    public CCResult selectPartnerEstimateItem(
            @Parameter(description = "견적 항목 ID") @PathVariable Long estimateItemId,
            @RequestBody Map<String, Long> body) {
        return this.spEstimateService.selectPartnerEstimateItem(estimateItemId, body.get("partnerEstimateItemId"));
    }

    @Operation(summary = "협력사 견적 다중 선택", description = "여러 견적 항목의 협력사 견적을 일괄 선택합니다")
    @JwtAuth
    @PostMapping("/items/_batch/selectPartner")
    public CCResult selectPartnerEstimateItems(@RequestBody List<SpEstimatePartnerSelectionDTO> selections) {
        return this.spEstimateService.selectPartnerEstimateItems(selections);
    }
}
