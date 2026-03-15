package kr.co.samplepcb.xpse.pojo;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Date;
import java.util.List;

@Schema(description = "견적서 상세 응답")
public class SpEstimateDetailDTO {

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
    @Schema(description = "배송비")
    private Integer shippingFee;
    @Schema(description = "관리비")
    private Integer managementFee;
    @Schema(description = "총액")
    private Integer totalAmount;
    @Schema(description = "최종 금액")
    private Integer finalAmount;
    @Schema(description = "메모")
    private String memo;
    @Schema(description = "글로벌 마진율")
    private Integer globalMarginRate;
    @Schema(description = "작성일")
    private Date writeDate;
    @Schema(description = "수정일")
    private Date modifyDate;

    @Schema(description = "견적 항목 리스트")
    private List<EstimateItemDTO> items;
    @Schema(description = "첨부파일 리스트")
    private List<FileDTO> files;

    @Schema(description = "견적 항목")
    public static class EstimateItemDTO {
        @Schema(description = "항목 ID")
        private Long id;
        @Schema(description = "PCB 부품 doc_id")
        private String pcbPartDocId;
        @Schema(description = "수량")
        private int qty;
        @Schema(description = "분석 메타 (JSON)")
        private Object analysisMeta;
        @Schema(description = "선택된 가격 (JSON)")
        private Object selectedPrice;
        @Schema(description = "선택된 협력사 견적 항목 ID")
        private Long selectedPartnerEstimateItemId;
        @Schema(description = "작성일")
        private Date writeDate;
        @Schema(description = "수정일")
        private Date modifyDate;
        @Schema(description = "PCB 부품 정보")
        private PcbPartDTO pcbPart;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getPcbPartDocId() { return pcbPartDocId; }
        public void setPcbPartDocId(String pcbPartDocId) { this.pcbPartDocId = pcbPartDocId; }
        public int getQty() { return qty; }
        public void setQty(int qty) { this.qty = qty; }
        public Object getAnalysisMeta() { return analysisMeta; }
        public void setAnalysisMeta(Object analysisMeta) { this.analysisMeta = analysisMeta; }
        public Object getSelectedPrice() { return selectedPrice; }
        public void setSelectedPrice(Object selectedPrice) { this.selectedPrice = selectedPrice; }
        public Long getSelectedPartnerEstimateItemId() { return selectedPartnerEstimateItemId; }
        public void setSelectedPartnerEstimateItemId(Long selectedPartnerEstimateItemId) { this.selectedPartnerEstimateItemId = selectedPartnerEstimateItemId; }
        public Date getWriteDate() { return writeDate; }
        public void setWriteDate(Date writeDate) { this.writeDate = writeDate; }
        public Date getModifyDate() { return modifyDate; }
        public void setModifyDate(Date modifyDate) { this.modifyDate = modifyDate; }
        public PcbPartDTO getPcbPart() { return pcbPart; }
        public void setPcbPart(PcbPartDTO pcbPart) { this.pcbPart = pcbPart; }
    }

    @Schema(description = "PCB 부품 정보")
    public static class PcbPartDTO {
        private String docId;
        private String partName;
        private String description;
        private String manufacturerName;
        private String partsPackaging;
        private Integer moq;
        private Integer price;
        private String offerName;
        private String largeCategory;
        private String mediumCategory;
        private String smallCategory;

        public String getDocId() { return docId; }
        public void setDocId(String docId) { this.docId = docId; }
        public String getPartName() { return partName; }
        public void setPartName(String partName) { this.partName = partName; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getManufacturerName() { return manufacturerName; }
        public void setManufacturerName(String manufacturerName) { this.manufacturerName = manufacturerName; }
        public String getPartsPackaging() { return partsPackaging; }
        public void setPartsPackaging(String partsPackaging) { this.partsPackaging = partsPackaging; }
        public Integer getMoq() { return moq; }
        public void setMoq(Integer moq) { this.moq = moq; }
        public Integer getPrice() { return price; }
        public void setPrice(Integer price) { this.price = price; }
        public String getOfferName() { return offerName; }
        public void setOfferName(String offerName) { this.offerName = offerName; }
        public String getLargeCategory() { return largeCategory; }
        public void setLargeCategory(String largeCategory) { this.largeCategory = largeCategory; }
        public String getMediumCategory() { return mediumCategory; }
        public void setMediumCategory(String mediumCategory) { this.mediumCategory = mediumCategory; }
        public String getSmallCategory() { return smallCategory; }
        public void setSmallCategory(String smallCategory) { this.smallCategory = smallCategory; }
    }

    @Schema(description = "첨부파일")
    public static class FileDTO {
        @Schema(description = "파일 ID")
        private Long id;
        @Schema(description = "업로드된 파일명")
        private String uploadFileName;
        @Schema(description = "원본 파일명")
        private String originFileName;
        @Schema(description = "경로 토큰")
        private String pathToken;
        @Schema(description = "파일 크기 (bytes)")
        private long size;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getUploadFileName() { return uploadFileName; }
        public void setUploadFileName(String uploadFileName) { this.uploadFileName = uploadFileName; }
        public String getOriginFileName() { return originFileName; }
        public void setOriginFileName(String originFileName) { this.originFileName = originFileName; }
        public String getPathToken() { return pathToken; }
        public void setPathToken(String pathToken) { this.pathToken = pathToken; }
        public long getSize() { return size; }
        public void setSize(long size) { this.size = size; }
    }

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
    public Integer getShippingFee() { return shippingFee; }
    public void setShippingFee(Integer shippingFee) { this.shippingFee = shippingFee; }
    public Integer getManagementFee() { return managementFee; }
    public void setManagementFee(Integer managementFee) { this.managementFee = managementFee; }
    public Integer getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Integer totalAmount) { this.totalAmount = totalAmount; }
    public Integer getFinalAmount() { return finalAmount; }
    public void setFinalAmount(Integer finalAmount) { this.finalAmount = finalAmount; }
    public String getMemo() { return memo; }
    public void setMemo(String memo) { this.memo = memo; }
    public Integer getGlobalMarginRate() { return globalMarginRate; }
    public void setGlobalMarginRate(Integer globalMarginRate) { this.globalMarginRate = globalMarginRate; }
    public Date getWriteDate() { return writeDate; }
    public void setWriteDate(Date writeDate) { this.writeDate = writeDate; }
    public Date getModifyDate() { return modifyDate; }
    public void setModifyDate(Date modifyDate) { this.modifyDate = modifyDate; }
    public List<EstimateItemDTO> getItems() { return items; }
    public void setItems(List<EstimateItemDTO> items) { this.items = items; }
    public List<FileDTO> getFiles() { return files; }
    public void setFiles(List<FileDTO> files) { this.files = files; }
}
