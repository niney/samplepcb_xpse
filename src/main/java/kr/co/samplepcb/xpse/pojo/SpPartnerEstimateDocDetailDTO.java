package kr.co.samplepcb.xpse.pojo;

import io.swagger.v3.oas.annotations.media.Schema;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import java.util.Date;
import java.util.List;

@Schema(description = "협력사용 견적서 상세 응답 (estimate_item + partner_estimate_item flat)")
public class SpPartnerEstimateDocDetailDTO {

    private static final ObjectMapper objectMapper = JsonMapper.builder().build();

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
    @Schema(description = "협력사 이름")
    private String partnerName;
    @Schema(description = "협력사 회원번호")
    private int mbNo;
    @Schema(description = "작성일")
    private Date writeDate;
    @Schema(description = "수정일")
    private Date modifyDate;

    @Schema(description = "견적 항목 리스트 (협력사 견적 정보 포함)")
    private List<ItemDTO> items;
    @Schema(description = "견적서 첨부파일")
    private List<FileDTO> files;

    @Schema(description = "견적 항목 (estimate_item + pcb_parts + partner_estimate_item flat)")
    public static class ItemDTO {
        // sp_estimate_item
        @Schema(description = "견적 항목 ID")
        private Long estimateItemId;
        @Schema(description = "PCB 부품 doc_id")
        private String pcbPartDocId;
        @Schema(description = "수량")
        private int qty;
        @Schema(description = "분석 메타 (JSON)")
        private Object analysisMeta;
        @Schema(description = "선택된 가격 (JSON)")
        private Object selectedPrice;

        // pcb_parts
        @Schema(description = "부품명")
        private String partName;
        @Schema(description = "부품 설명")
        private String description;
        @Schema(description = "제조사명")
        private String manufacturerName;
        @Schema(description = "포장 형태")
        private String partsPackaging;
        @Schema(description = "부품 크기")
        private String size;
        @Schema(description = "최소 주문 수량")
        private Integer moq;
        @Schema(description = "가격")
        private Integer price;
        @Schema(description = "공급자명")
        private String offerName;

        // sp_partner_estimate_item
        @Schema(description = "협력사 견적 항목 ID")
        private Long partnerEstimateItemId;
        @Schema(description = "협력사 선택 가격 (JSON)")
        private Object partnerSelectedPrice;
        @Schema(description = "협력사 상태")
        private String partnerStatus;
        @Schema(description = "협력사 메모")
        private String partnerMemo;
        @Schema(description = "협력사 Date Code")
        private String partnerDateCode;
        @Schema(description = "협력사 납기일")
        private Date partnerDeliveryDate;
        @Schema(description = "협력사 작성일")
        private Date partnerWriteDate;
        @Schema(description = "협력사 수정일")
        private Date partnerModifyDate;

        @Schema(description = "협력사 항목 첨부파일")
        private List<FileDTO> partnerFiles;

        public ItemDTO() {}

        public ItemDTO(Long estimateItemId, String pcbPartDocId, int qty, String analysisMeta, String selectedPrice,
                       String partName, String description, String manufacturerName, String partsPackaging, String size,
                       Integer moq, Integer price, String offerName,
                       Long partnerEstimateItemId, String partnerSelectedPrice, String partnerStatus,
                       String partnerMemo, String partnerDateCode, Date partnerDeliveryDate,
                       Date partnerWriteDate, Date partnerModifyDate) {
            this.estimateItemId = estimateItemId;
            this.pcbPartDocId = pcbPartDocId;
            this.qty = qty;
            this.analysisMeta = parseJson(analysisMeta);
            this.selectedPrice = parseJson(selectedPrice);
            this.partName = partName;
            this.description = description;
            this.manufacturerName = manufacturerName;
            this.partsPackaging = partsPackaging;
            this.size = size;
            this.moq = moq;
            this.price = price;
            this.offerName = offerName;
            this.partnerEstimateItemId = partnerEstimateItemId;
            this.partnerSelectedPrice = parseJson(partnerSelectedPrice);
            this.partnerStatus = partnerStatus;
            this.partnerMemo = partnerMemo;
            this.partnerDateCode = partnerDateCode;
            this.partnerDeliveryDate = partnerDeliveryDate;
            this.partnerWriteDate = partnerWriteDate;
            this.partnerModifyDate = partnerModifyDate;
        }

        public Long getEstimateItemId() { return estimateItemId; }
        public void setEstimateItemId(Long estimateItemId) { this.estimateItemId = estimateItemId; }
        public String getPcbPartDocId() { return pcbPartDocId; }
        public void setPcbPartDocId(String pcbPartDocId) { this.pcbPartDocId = pcbPartDocId; }
        public int getQty() { return qty; }
        public void setQty(int qty) { this.qty = qty; }
        public Object getAnalysisMeta() { return analysisMeta; }
        public void setAnalysisMeta(Object analysisMeta) { this.analysisMeta = analysisMeta; }
        public Object getSelectedPrice() { return selectedPrice; }
        public void setSelectedPrice(Object selectedPrice) { this.selectedPrice = selectedPrice; }
        public String getPartName() { return partName; }
        public void setPartName(String partName) { this.partName = partName; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getManufacturerName() { return manufacturerName; }
        public void setManufacturerName(String manufacturerName) { this.manufacturerName = manufacturerName; }
        public String getPartsPackaging() { return partsPackaging; }
        public void setPartsPackaging(String partsPackaging) { this.partsPackaging = partsPackaging; }
        public String getSize() { return size; }
        public void setSize(String size) { this.size = size; }
        public Integer getMoq() { return moq; }
        public void setMoq(Integer moq) { this.moq = moq; }
        public Integer getPrice() { return price; }
        public void setPrice(Integer price) { this.price = price; }
        public String getOfferName() { return offerName; }
        public void setOfferName(String offerName) { this.offerName = offerName; }
        public Long getPartnerEstimateItemId() { return partnerEstimateItemId; }
        public void setPartnerEstimateItemId(Long partnerEstimateItemId) { this.partnerEstimateItemId = partnerEstimateItemId; }
        public Object getPartnerSelectedPrice() { return partnerSelectedPrice; }
        public void setPartnerSelectedPrice(Object partnerSelectedPrice) { this.partnerSelectedPrice = partnerSelectedPrice; }
        public String getPartnerStatus() { return partnerStatus; }
        public void setPartnerStatus(String partnerStatus) { this.partnerStatus = partnerStatus; }
        public String getPartnerMemo() { return partnerMemo; }
        public void setPartnerMemo(String partnerMemo) { this.partnerMemo = partnerMemo; }
        public String getPartnerDateCode() { return partnerDateCode; }
        public void setPartnerDateCode(String partnerDateCode) { this.partnerDateCode = partnerDateCode; }
        public Date getPartnerDeliveryDate() { return partnerDeliveryDate; }
        public void setPartnerDeliveryDate(Date partnerDeliveryDate) { this.partnerDeliveryDate = partnerDeliveryDate; }
        public Date getPartnerWriteDate() { return partnerWriteDate; }
        public void setPartnerWriteDate(Date partnerWriteDate) { this.partnerWriteDate = partnerWriteDate; }
        public Date getPartnerModifyDate() { return partnerModifyDate; }
        public void setPartnerModifyDate(Date partnerModifyDate) { this.partnerModifyDate = partnerModifyDate; }
        public List<FileDTO> getPartnerFiles() { return partnerFiles; }
        public void setPartnerFiles(List<FileDTO> partnerFiles) { this.partnerFiles = partnerFiles; }
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
    public Integer getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Integer totalAmount) { this.totalAmount = totalAmount; }
    public Integer getFinalAmount() { return finalAmount; }
    public void setFinalAmount(Integer finalAmount) { this.finalAmount = finalAmount; }
    public String getMemo() { return memo; }
    public void setMemo(String memo) { this.memo = memo; }
    public String getPartnerName() { return partnerName; }
    public void setPartnerName(String partnerName) { this.partnerName = partnerName; }
    public int getMbNo() { return mbNo; }
    public void setMbNo(int mbNo) { this.mbNo = mbNo; }
    public Date getWriteDate() { return writeDate; }
    public void setWriteDate(Date writeDate) { this.writeDate = writeDate; }
    public Date getModifyDate() { return modifyDate; }
    public void setModifyDate(Date modifyDate) { this.modifyDate = modifyDate; }
    public List<ItemDTO> getItems() { return items; }
    public void setItems(List<ItemDTO> items) { this.items = items; }
    public List<FileDTO> getFiles() { return files; }
    public void setFiles(List<FileDTO> files) { this.files = files; }

    private static Object parseJson(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return objectMapper.readValue(value, Object.class);
        } catch (JacksonException e) {
            return value;
        }
    }
}
