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
import kr.co.samplepcb.xpse.pojo.SpPartnerEstimateItemDetailDTO;
import kr.co.samplepcb.xpse.pojo.SpPartnerEstimateItemListDTO;
import kr.co.samplepcb.xpse.pojo.SpPartnerEstimateItemSearchParam;
import kr.co.samplepcb.xpse.security.JwtAuth;
import kr.co.samplepcb.xpse.service.SpEstimateService;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "협력사 주문", description = "협력사 주문 관리 API")
@SecurityRequirement(name = "Bearer Token")
@RestController
@RequestMapping("/api/spPartnerOrders")
public class SpPartnerOrderResource {

    private final SpEstimateService spEstimateService;

    public SpPartnerOrderResource(SpEstimateService spEstimateService) {
        this.spEstimateService = spEstimateService;
    }

    @Operation(summary = "협력사 주문 상세 조회", description = "견적 항목 ID와 파트너 회원번호로 상세 정보를 조회합니다")
    @JwtAuth
    @GetMapping("/{estimateItemId}/{mbNo}")
    public CCObjectResult<SpPartnerEstimateItemDetailDTO> getDetail(@Parameter(description = "견적 항목 ID") @PathVariable Long estimateItemId,
                              @Parameter(description = "파트너 회원번호") @PathVariable int mbNo) {
        return this.spEstimateService.getPartnerEstimateItemDetail(estimateItemId, mbNo);
    }

    @Operation(summary = "협력사 주문 검색", description = "협력사 주문 목록을 견적항목/파트너/상태 조건으로 검색합니다")
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

    @Operation(summary = "협력사 주문 단건 생성", description = "협력사 주문을 단건으로 생성합니다")
    @JwtAuth
    @PostMapping
    public CCObjectResult<SpPartnerEstimateItem> create(@RequestBody SpPartnerEstimateItemCreateDTO createDTO) {
        return this.spEstimateService.createPartnerOrder(createDTO);
    }

    @Operation(summary = "협력사 주문 다중 생성", description = "협력사 주문을 다중으로 일괄 생성합니다")
    @JwtAuth
    @PostMapping("/_batch")
    public CCObjectResult<List<SpPartnerEstimateItemCreateDTO>> createBatch(@RequestBody List<SpPartnerEstimateItemCreateDTO> createDTOs) {
        return this.spEstimateService.createPartnerOrderBatch(createDTOs);
    }
}
