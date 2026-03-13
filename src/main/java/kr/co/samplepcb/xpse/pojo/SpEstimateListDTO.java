package kr.co.samplepcb.xpse.pojo;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Date;

@Schema(description = "견적서 목록 응답")
public class SpEstimateListDTO {

    @Schema(description = "견적서 ID")
    private Long id;
    @Schema(description = "아이템 ID")
    private String itId;
    @Schema(description = "상태")
    private String status;
    @Schema(description = "예상 납기")
    private String expectedDelivery;
    @Schema(description = "총액")
    private Integer totalAmount;
    @Schema(description = "최종 금액")
    private Integer finalAmount;
    @Schema(description = "항목 수")
    private int itemCount;
    @Schema(description = "작성일")
    private Date writeDate;
    @Schema(description = "수정일")
    private Date modifyDate;

    // === getter / setter ===

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getItId() { return itId; }
    public void setItId(String itId) { this.itId = itId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getExpectedDelivery() { return expectedDelivery; }
    public void setExpectedDelivery(String expectedDelivery) { this.expectedDelivery = expectedDelivery; }
    public Integer getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Integer totalAmount) { this.totalAmount = totalAmount; }
    public Integer getFinalAmount() { return finalAmount; }
    public void setFinalAmount(Integer finalAmount) { this.finalAmount = finalAmount; }
    public int getItemCount() { return itemCount; }
    public void setItemCount(int itemCount) { this.itemCount = itemCount; }
    public Date getWriteDate() { return writeDate; }
    public void setWriteDate(Date writeDate) { this.writeDate = writeDate; }
    public Date getModifyDate() { return modifyDate; }
    public void setModifyDate(Date modifyDate) { this.modifyDate = modifyDate; }
}
