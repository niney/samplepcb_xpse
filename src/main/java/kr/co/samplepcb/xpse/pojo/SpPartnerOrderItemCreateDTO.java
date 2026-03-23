package kr.co.samplepcb.xpse.pojo;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Date;

@Schema(description = "협력사 발주 항목 생성/수정 요청")
public class SpPartnerOrderItemCreateDTO {

    @Schema(description = "견적 항목 ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long estimateItemId;

    @Schema(description = "파트너 회원번호", requiredMode = Schema.RequiredMode.REQUIRED)
    private int mbNo;

    @Schema(description = "선택된 가격 (JSON text)")
    private String selectedPrice;

    @Schema(description = "발주 상태")
    private String status;

    @Schema(description = "메모")
    private String memo;

    @Schema(description = "데이트 코드")
    private String dateCode;

    @Schema(description = "납기일")
    private Date deliveryDate;

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
    public String getDateCode() { return dateCode; }
    public void setDateCode(String dateCode) { this.dateCode = dateCode; }
    public Date getDeliveryDate() { return deliveryDate; }
    public void setDeliveryDate(Date deliveryDate) { this.deliveryDate = deliveryDate; }
}
