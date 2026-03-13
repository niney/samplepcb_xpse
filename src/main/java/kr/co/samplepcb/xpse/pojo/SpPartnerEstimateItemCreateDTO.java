package kr.co.samplepcb.xpse.pojo;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "협력사 견적 항목 생성/수정 요청")
public class SpPartnerEstimateItemCreateDTO {

    @Schema(description = "견적 항목 ID")
    private Long estimateItemId;

    @Schema(description = "파트너 회원번호", requiredMode = Schema.RequiredMode.REQUIRED)
    private int mbNo;

    @Schema(description = "선택된 가격 (JSON text)")
    private String selectedPrice;

    @Schema(description = "주문 상태")
    private String status;

    @Schema(description = "메모")
    private String memo;

    public Long getEstimateItemId() { return estimateItemId; }
    public void setEstimateItemId(Long estimateItemId) { this.estimateItemId = estimateItemId; }
    public int getMbNo() { return mbNo; }
    public void setMbNo(int mbNo) { this.mbNo = mbNo; }
    public String getSelectedPrice() { return selectedPrice; }
    public void setSelectedPrice(String selectedPrice) { this.selectedPrice = selectedPrice; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getMemo() { return memo; }
    public void setMemo(String memo) { this.memo = memo; }
}
