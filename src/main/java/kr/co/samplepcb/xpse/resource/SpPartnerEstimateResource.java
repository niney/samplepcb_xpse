package kr.co.samplepcb.xpse.resource;

import coolib.common.CCObjectResult;
import coolib.common.CCPagingResult;
import coolib.common.CCResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.samplepcb.xpse.domain.entity.SpPartnerEstimateItem;
import kr.co.samplepcb.xpse.pojo.SpPartnerEstimateDocListDTO;
import kr.co.samplepcb.xpse.pojo.SpPartnerEstimateItemCreateDTO;
import kr.co.samplepcb.xpse.pojo.SpPartnerEstimateItemDeleteKeyDTO;
import kr.co.samplepcb.xpse.pojo.SpPartnerEstimateItemDetailDTO;
import kr.co.samplepcb.xpse.pojo.SpPartnerEstimateItemListDTO;
import kr.co.samplepcb.xpse.pojo.SpPartnerEstimateItemSearchParam;
import kr.co.samplepcb.xpse.pojo.SpEstimateListDTO;
import kr.co.samplepcb.xpse.pojo.SpEstimateSearchParam;
import kr.co.samplepcb.xpse.pojo.SpPartnerEstimateDocDetailDTO;
import kr.co.samplepcb.xpse.pojo.SpPartnerEstimateDocUpdateDTO;
import kr.co.samplepcb.xpse.security.JwtAuth;
import kr.co.samplepcb.xpse.service.SpEstimateService;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "협력사 견적", description = "협력사 견적 관리 API")
@SecurityRequirement(name = "Bearer Token")
@RestController
@RequestMapping("/api/spPartnerEstimates")
public class SpPartnerEstimateResource {

    private final SpEstimateService spEstimateService;

    public SpPartnerEstimateResource(SpEstimateService spEstimateService) {
        this.spEstimateService = spEstimateService;
    }

    @Operation(summary = "협력사 견적 상세 조회", description = "견적 항목 ID와 파트너 회원번호로 상세 정보를 조회합니다")
    @JwtAuth
    @GetMapping("/{estimateItemId}/{mbNo}")
    public CCObjectResult<SpPartnerEstimateItemDetailDTO> getDetail(@Parameter(description = "견적 항목 ID") @PathVariable Long estimateItemId,
                              @Parameter(description = "파트너 회원번호") @PathVariable int mbNo) {
        return this.spEstimateService.getPartnerEstimateItemDetail(estimateItemId, mbNo);
    }

    @Operation(summary = "협력사 견적 검색", description = "협력사 견적 목록을 견적항목/파트너/상태 조건으로 검색합니다")
    @JwtAuth
    @GetMapping("/_search")
    public CCPagingResult<SpPartnerEstimateItemListDTO> search(Pageable pageable, SpPartnerEstimateItemSearchParam searchParam) {
        return this.spEstimateService.searchPartnerEstimateItems(pageable, searchParam);
    }

    @Operation(summary = "협력사 견적서 목록 검색", description = "협력사가 참여한 견적서(estimate document) 목록을 검색합니다")
    @JwtAuth
    @GetMapping("/estimates/_search")
    public CCPagingResult<SpPartnerEstimateDocListDTO> searchEstimateDocs(Pageable pageable, SpPartnerEstimateItemSearchParam searchParam) {
        return this.spEstimateService.searchPartnerEstimateDocs(pageable, searchParam);
    }

    @Operation(summary = "협력사용 견적서 목록 조회", description = "협력사에 배정된 견적서(sp_estimate_document) 목록을 조회합니다 (마진율 미노출)")
    @JwtAuth
    @GetMapping("/estimateDocuments/_search")
    public CCPagingResult<SpEstimateListDTO> searchEstimateDocuments(Pageable pageable, SpEstimateSearchParam searchParam,
                                                                     @Parameter(description = "파트너 회원번호") @RequestParam int mbNo) {
        return this.spEstimateService.searchEstimateDocsForPartner(pageable, searchParam, mbNo);
    }

    @Operation(summary = "협력사용 견적서 상세 조회", description = "견적서 상세를 조회합니다 (마진율 미노출, 협력사 견적항목 포함, 협력사별 전체 조회)")
    @JwtAuth
    @GetMapping("/estimateDocuments/{id}")
    public CCObjectResult<List<SpPartnerEstimateDocDetailDTO>> getEstimateDocumentDetail(@Parameter(description = "견적서 ID") @PathVariable Long id) {
        return this.spEstimateService.getEstimateDocDetailForAllPartners(id);
    }

    @Operation(summary = "협력사용 견적서 상세 수정", description = "협력사 견적서 상세를 수정합니다 (문서 레벨 + 항목 레벨)")
    @JwtAuth
    @PostMapping("/estimateDocuments/{id}")
    public CCObjectResult<SpPartnerEstimateDocDetailDTO> updateEstimateDocumentDetail(@Parameter(description = "견적서 ID") @PathVariable Long id,
                                                                            @RequestBody SpPartnerEstimateDocUpdateDTO updateDTO) {
        return this.spEstimateService.updateEstimateDocForPartner(id, updateDTO);
    }

    @Operation(summary = "협력사용 견적서 상세 조회 (partnerEstimateDocument 기준)", description = "partnerEstimateDocument ID로 견적서 상세를 조회합니다")
    @JwtAuth
    @GetMapping("/partnerEstimateDocuments/{pedId}")
    public CCObjectResult<SpPartnerEstimateDocDetailDTO> getPartnerEstimateDocumentDetail(@Parameter(description = "협력사 견적서 ID") @PathVariable Long pedId) {
        return this.spEstimateService.getEstimateDocDetailByPartnerDoc(pedId);
    }

    @Operation(summary = "협력사용 견적서 상세 수정 (partnerEstimateDocument 기준)", description = "partnerEstimateDocument ID로 견적서 상세를 수정합니다")
    @JwtAuth
    @PostMapping("/partnerEstimateDocuments/{pedId}")
    public CCObjectResult<SpPartnerEstimateDocDetailDTO> updatePartnerEstimateDocumentDetail(@Parameter(description = "협력사 견적서 ID") @PathVariable Long pedId,
                                                                                   @RequestBody SpPartnerEstimateDocUpdateDTO updateDTO) {
        return this.spEstimateService.updateEstimateDocByPartnerDoc(pedId, updateDTO);
    }

    @Operation(summary = "협력사 견적서 상태 수정", description = "partnerEstimateDocument의 status를 수정합니다")
    @JwtAuth
    @PostMapping("/partnerEstimateDocuments/{pedId}/status")
    public CCResult updatePartnerEstimateDocStatus(@Parameter(description = "협력사 견적서 ID") @PathVariable Long pedId,
                                                   @Parameter(description = "변경할 상태값") @RequestParam String status) {
        return this.spEstimateService.updatePartnerEstimateDocStatus(pedId, status);
    }

    @Operation(summary = "협력사 견적 단건 생성", description = "협력사 견적을 단건으로 생성합니다")
    @JwtAuth
    @PostMapping
    public CCObjectResult<SpPartnerEstimateItem> create(@RequestBody SpPartnerEstimateItemCreateDTO createDTO) {
        return this.spEstimateService.createPartnerOrder(createDTO);
    }

    @Operation(summary = "협력사 견적 다중 생성", description = "협력사 견적을 다중으로 일괄 생성합니다")
    @JwtAuth
    @PostMapping("/_batch")
    public CCObjectResult<List<SpPartnerEstimateItemCreateDTO>> createBatch(@RequestBody List<SpPartnerEstimateItemCreateDTO> createDTOs) {
        return this.spEstimateService.createPartnerOrderBatch(createDTOs);
    }

    @Operation(summary = "협력사 견적 다중 삭제", description = "견적 항목/파트너 조합으로 협력사 견적을 다중으로 삭제합니다")
    @JwtAuth
    @PostMapping("/_batch/delete")
    public CCResult deleteBatch(@RequestBody List<SpPartnerEstimateItemDeleteKeyDTO> deleteDTOs) {
        return this.spEstimateService.deletePartnerOrderBatch(deleteDTOs);
    }
}
