package kr.co.samplepcb.xpse.pojo;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Date;
import java.util.List;

@Schema(description = "견적서 목록 응답")
public class SpEstimateListDTO {

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
    private int itemCount;
    @Schema(description = "작성일")
    private Date writeDate;
    @Schema(description = "수정일")
    private Date modifyDate;

    @Schema(description = "회원 아이디")
    private String mbId;
    @Schema(description = "회원 이름")
    private String mbName;
    @Schema(description = "회원 이메일")
    private String mbEmail;
    @Schema(description = "회원 연락처")
    private String mbHp;
    @Schema(description = "회원 전화번호")
    private String mbTel;

    @Schema(description = "협력사 견적서 목록")
    private List<PartnerEstimateDTO> partnerEstimates;

    @Schema(description = "협력사 발주서 목록")
    private List<PartnerOrderDTO> partnerOrders;

    // === getter / setter ===

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
    public int getItemCount() { return itemCount; }
    public void setItemCount(int itemCount) { this.itemCount = itemCount; }
    public Date getWriteDate() { return writeDate; }
    public void setWriteDate(Date writeDate) { this.writeDate = writeDate; }
    public Date getModifyDate() { return modifyDate; }
    public void setModifyDate(Date modifyDate) { this.modifyDate = modifyDate; }
    public String getMbId() { return mbId; }
    public void setMbId(String mbId) { this.mbId = mbId; }
    public String getMbName() { return mbName; }
    public void setMbName(String mbName) { this.mbName = mbName; }
    public String getMbEmail() { return mbEmail; }
    public void setMbEmail(String mbEmail) { this.mbEmail = mbEmail; }
    public String getMbHp() { return mbHp; }
    public void setMbHp(String mbHp) { this.mbHp = mbHp; }
    public String getMbTel() { return mbTel; }
    public void setMbTel(String mbTel) { this.mbTel = mbTel; }
    public List<PartnerEstimateDTO> getPartnerEstimates() { return partnerEstimates; }
    public void setPartnerEstimates(List<PartnerEstimateDTO> partnerEstimates) { this.partnerEstimates = partnerEstimates; }
    public List<PartnerOrderDTO> getPartnerOrders() { return partnerOrders; }
    public void setPartnerOrders(List<PartnerOrderDTO> partnerOrders) { this.partnerOrders = partnerOrders; }

    @Schema(description = "협력사 견적서")
    public static class PartnerEstimateDTO {

        @Schema(description = "협력사 견적서 ID")
        private Long id;
        @Schema(description = "협력사 회원 번호")
        private int mbNo;
        @Schema(description = "협력사명")
        private String partnerName;
        @Schema(description = "상태")
        private String status;
        @Schema(description = "견적 가격")
        private Integer estimatePrice;
        @Schema(description = "메모")
        private String memo;
        @Schema(description = "납기일")
        private Date deliveryDate;
        @Schema(description = "작성일")
        private Date writeDate;
        @Schema(description = "수정일")
        private Date modifyDate;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public int getMbNo() { return mbNo; }
        public void setMbNo(int mbNo) { this.mbNo = mbNo; }
        public String getPartnerName() { return partnerName; }
        public void setPartnerName(String partnerName) { this.partnerName = partnerName; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public Integer getEstimatePrice() { return estimatePrice; }
        public void setEstimatePrice(Integer estimatePrice) { this.estimatePrice = estimatePrice; }
        public String getMemo() { return memo; }
        public void setMemo(String memo) { this.memo = memo; }
        public Date getDeliveryDate() { return deliveryDate; }
        public void setDeliveryDate(Date deliveryDate) { this.deliveryDate = deliveryDate; }
        public Date getWriteDate() { return writeDate; }
        public void setWriteDate(Date writeDate) { this.writeDate = writeDate; }
        public Date getModifyDate() { return modifyDate; }
        public void setModifyDate(Date modifyDate) { this.modifyDate = modifyDate; }
    }

    @Schema(description = "협력사 발주서")
    public static class PartnerOrderDTO {

        @Schema(description = "협력사 발주서 ID")
        private Long id;
        @Schema(description = "협력사 회원 번호")
        private int mbNo;
        @Schema(description = "협력사명")
        private String partnerName;
        @Schema(description = "상태")
        private String status;
        @Schema(description = "발주 가격")
        private Integer orderPrice;
        @Schema(description = "메모")
        private String memo;
        @Schema(description = "납기일")
        private Date deliveryDate;
        @Schema(description = "작성일")
        private Date writeDate;
        @Schema(description = "수정일")
        private Date modifyDate;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public int getMbNo() { return mbNo; }
        public void setMbNo(int mbNo) { this.mbNo = mbNo; }
        public String getPartnerName() { return partnerName; }
        public void setPartnerName(String partnerName) { this.partnerName = partnerName; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public Integer getOrderPrice() { return orderPrice; }
        public void setOrderPrice(Integer orderPrice) { this.orderPrice = orderPrice; }
        public String getMemo() { return memo; }
        public void setMemo(String memo) { this.memo = memo; }
        public Date getDeliveryDate() { return deliveryDate; }
        public void setDeliveryDate(Date deliveryDate) { this.deliveryDate = deliveryDate; }
        public Date getWriteDate() { return writeDate; }
        public void setWriteDate(Date writeDate) { this.writeDate = writeDate; }
        public Date getModifyDate() { return modifyDate; }
        public void setModifyDate(Date modifyDate) { this.modifyDate = modifyDate; }
    }
}
