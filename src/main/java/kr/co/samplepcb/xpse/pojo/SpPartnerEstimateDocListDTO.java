package kr.co.samplepcb.xpse.pojo;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Date;

@Schema(description = "파트너별 견적서 목록")
public class SpPartnerEstimateDocListDTO {

    @Schema(description = "견적서 ID")
    private Long id;
    @Schema(description = "아이템 ID")
    private String itId;
    @Schema(description = "아이템 이름")
    private String itName;
    @Schema(description = "상태")
    private String status;
    @Schema(description = "예상 납기")
    private String expectedDelivery;
    @Schema(description = "총액")
    private Integer totalAmount;
    @Schema(description = "최종 금액")
    private Integer finalAmount;
    @Schema(description = "메모")
    private String memo;
    @Schema(description = "글로벌 마진율")
    private Integer globalMarginRate;
    @Schema(description = "항목 수")
    private long itemCount;
    @Schema(description = "작성일")
    private Date writeDate;
    @Schema(description = "수정일")
    private Date modifyDate;

    public SpPartnerEstimateDocListDTO() {}

    public SpPartnerEstimateDocListDTO(Long id, String itId, String itName,
                                        String status, String expectedDelivery,
                                        Integer totalAmount, Integer finalAmount,
                                        String memo, Integer globalMarginRate,
                                        long itemCount,
                                        Date writeDate, Date modifyDate) {
        this.id = id;
        this.itId = itId;
        this.itName = itName;
        this.status = status;
        this.expectedDelivery = expectedDelivery;
        this.totalAmount = totalAmount;
        this.finalAmount = finalAmount;
        this.memo = memo;
        this.globalMarginRate = globalMarginRate;
        this.itemCount = itemCount;
        this.writeDate = writeDate;
        this.modifyDate = modifyDate;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getItId() { return itId; }
    public void setItId(String itId) { this.itId = itId; }
    public String getItName() { return itName; }
    public void setItName(String itName) { this.itName = itName; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getExpectedDelivery() { return expectedDelivery; }
    public void setExpectedDelivery(String expectedDelivery) { this.expectedDelivery = expectedDelivery; }
    public Integer getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Integer totalAmount) { this.totalAmount = totalAmount; }
    public Integer getFinalAmount() { return finalAmount; }
    public void setFinalAmount(Integer finalAmount) { this.finalAmount = finalAmount; }
    public String getMemo() { return memo; }
    public void setMemo(String memo) { this.memo = memo; }
    public Integer getGlobalMarginRate() { return globalMarginRate; }
    public void setGlobalMarginRate(Integer globalMarginRate) { this.globalMarginRate = globalMarginRate; }
    public long getItemCount() { return itemCount; }
    public void setItemCount(long itemCount) { this.itemCount = itemCount; }
    public Date getWriteDate() { return writeDate; }
    public void setWriteDate(Date writeDate) { this.writeDate = writeDate; }
    public Date getModifyDate() { return modifyDate; }
    public void setModifyDate(Date modifyDate) { this.modifyDate = modifyDate; }
}
