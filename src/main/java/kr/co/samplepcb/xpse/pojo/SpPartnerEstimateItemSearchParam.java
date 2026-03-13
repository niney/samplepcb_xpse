package kr.co.samplepcb.xpse.pojo;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "협력사 견적 항목 검색 파라미터")
public class SpPartnerEstimateItemSearchParam extends SearchParam {

    @Schema(description = "견적 항목 ID")
    private Long estimateItemId;
    @Schema(description = "협력사 견적서 ID")
    private Long partnerEstimateDocumentId;
    @Schema(description = "파트너 회원번호")
    private Integer mbNo;
    @Schema(description = "주문 상태")
    private String status;

    public Long getEstimateItemId() { return estimateItemId; }
    public void setEstimateItemId(Long estimateItemId) { this.estimateItemId = estimateItemId; }
    public Long getPartnerEstimateDocumentId() { return partnerEstimateDocumentId; }
    public void setPartnerEstimateDocumentId(Long partnerEstimateDocumentId) { this.partnerEstimateDocumentId = partnerEstimateDocumentId; }
    public Integer getMbNo() { return mbNo; }
    public void setMbNo(Integer mbNo) { this.mbNo = mbNo; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
