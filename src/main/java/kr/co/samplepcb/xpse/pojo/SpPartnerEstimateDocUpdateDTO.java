package kr.co.samplepcb.xpse.pojo;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Date;
import java.util.List;

@Schema(description = "협력사용 견적서 수정 요청 DTO")
public class SpPartnerEstimateDocUpdateDTO {

    @Schema(description = "파트너 회원번호", requiredMode = Schema.RequiredMode.REQUIRED)
    private int mbNo;

    @Schema(description = "협력사 견적가")
    private Integer estimatePrice;

    @Schema(description = "협력사 상태")
    private String status;

    @Schema(description = "협력사 메모")
    private String memo;

    @Schema(description = "납기일")
    private Date deliveryDate;

    @Schema(description = "항목별 수정 내역")
    private List<ItemUpdateDTO> items;

    @Schema(description = "협력사 견적 항목 수정 DTO")
    public static class ItemUpdateDTO {
        @Schema(description = "견적 항목 ID (매칭 키)", requiredMode = Schema.RequiredMode.REQUIRED)
        private Long estimateItemId;

        @Schema(description = "협력사 선택 가격 (JSON text)")
        private String selectedPrice;

        @Schema(description = "협력사 상태")
        private String status;

        @Schema(description = "협력사 메모")
        private String memo;

        @Schema(description = "Date Code")
        private String dateCode;

        @Schema(description = "납기일")
        private Date deliveryDate;

        public Long getEstimateItemId() { return estimateItemId; }
        public void setEstimateItemId(Long estimateItemId) { this.estimateItemId = estimateItemId; }
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

    public int getMbNo() { return mbNo; }
    public void setMbNo(int mbNo) { this.mbNo = mbNo; }
    public Integer getEstimatePrice() { return estimatePrice; }
    public void setEstimatePrice(Integer estimatePrice) { this.estimatePrice = estimatePrice; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getMemo() { return memo; }
    public void setMemo(String memo) { this.memo = memo; }
    public Date getDeliveryDate() { return deliveryDate; }
    public void setDeliveryDate(Date deliveryDate) { this.deliveryDate = deliveryDate; }
    public List<ItemUpdateDTO> getItems() { return items; }
    public void setItems(List<ItemUpdateDTO> items) { this.items = items; }
}
