package kr.co.samplepcb.xpse.pojo;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Date;

@Schema(description = "파트너별 견적서 목록")
public class SpPartnerEstimateDocListDTO {

    @Schema(description = "협력사 견적서 ID")
    private Long id;
    @Schema(description = "원본 견적서 ID")
    private Long estimateDocumentId;
    @Schema(description = "아이템 ID")
    private String itId;
    @Schema(description = "아이템 이름")
    private String itName;
    @Schema(description = "협력사 견적서 상태")
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
    @Schema(description = "파트너 회원번호")
    private int mbNo;
    @Schema(description = "파트너 이름")
    private String partnerName;
    @Schema(description = "파트너 전화번호")
    private String partnerTel;
    @Schema(description = "파트너 휴대폰")
    private String partnerHp;
    @Schema(description = "파트너 이메일")
    private String partnerEmail;
    @Schema(description = "작성일")
    private Date writeDate;
    @Schema(description = "수정일")
    private Date modifyDate;

    public SpPartnerEstimateDocListDTO() {}

    public SpPartnerEstimateDocListDTO(Long id, Long estimateDocumentId, String itId, String itName,
                                        String status, String expectedDelivery,
                                        Integer totalAmount, Integer finalAmount,
                                        String memo, Integer globalMarginRate,
                                        long itemCount,
                                        int mbNo, String partnerName, String partnerTel,
                                        String partnerHp, String partnerEmail,
                                        Date writeDate, Date modifyDate) {
        this.id = id;
        this.estimateDocumentId = estimateDocumentId;
        this.itId = itId;
        this.itName = itName;
        this.status = status;
        this.expectedDelivery = expectedDelivery;
        this.totalAmount = totalAmount;
        this.finalAmount = finalAmount;
        this.memo = memo;
        this.globalMarginRate = globalMarginRate;
        this.itemCount = itemCount;
        this.mbNo = mbNo;
        this.partnerName = partnerName;
        this.partnerTel = partnerTel;
        this.partnerHp = partnerHp;
        this.partnerEmail = partnerEmail;
        this.writeDate = writeDate;
        this.modifyDate = modifyDate;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getEstimateDocumentId() { return estimateDocumentId; }
    public void setEstimateDocumentId(Long estimateDocumentId) { this.estimateDocumentId = estimateDocumentId; }
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
    public int getMbNo() { return mbNo; }
    public void setMbNo(int mbNo) { this.mbNo = mbNo; }
    public String getPartnerName() { return partnerName; }
    public void setPartnerName(String partnerName) { this.partnerName = partnerName; }
    public String getPartnerTel() { return partnerTel; }
    public void setPartnerTel(String partnerTel) { this.partnerTel = partnerTel; }
    public String getPartnerHp() { return partnerHp; }
    public void setPartnerHp(String partnerHp) { this.partnerHp = partnerHp; }
    public String getPartnerEmail() { return partnerEmail; }
    public void setPartnerEmail(String partnerEmail) { this.partnerEmail = partnerEmail; }
    public Date getWriteDate() { return writeDate; }
    public void setWriteDate(Date writeDate) { this.writeDate = writeDate; }
    public Date getModifyDate() { return modifyDate; }
    public void setModifyDate(Date modifyDate) { this.modifyDate = modifyDate; }
}
