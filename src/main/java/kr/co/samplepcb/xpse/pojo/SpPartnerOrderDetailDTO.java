package kr.co.samplepcb.xpse.pojo;

import io.swagger.v3.oas.annotations.media.Schema;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import java.util.Date;
import java.util.List;

@Schema(description = "협력사 발주서 상세 응답 (estimate_item + pcb_parts + partner_order_item flat)")
public class SpPartnerOrderDetailDTO {

    private static final ObjectMapper objectMapper = JsonMapper.builder().build();

    // 견적서 정보
    @Schema(description = "견적서 ID")
    private Long estimateDocumentId;
    @Schema(description = "아이템 ID")
    private String itId;
    @Schema(description = "아이템 이름")
    private String itName;
    @Schema(description = "견적서 상태")
    private String estimateStatus;

    // 발주서 정보
    @Schema(description = "발주서 ID")
    private Long id;
    @Schema(description = "협력사 회원번호")
    private int mbNo;
    @Schema(description = "협력사명")
    private String partnerName;
    @Schema(description = "발주서 상태")
    private String status;
    @Schema(description = "발주 총금액")
    private Integer orderPrice;
    @Schema(description = "메모")
    private String memo;
    @Schema(description = "납기일")
    private Date deliveryDate;
    @Schema(description = "작성일")
    private Date writeDate;
    @Schema(description = "수정일")
    private Date modifyDate;

    @Schema(description = "견적 항목 리스트 (발주 정보 포함)")
    private List<ItemDTO> items;

    @Schema(description = "견적 항목 (estimate_item + pcb_parts + partner_order_item flat)")
    public static class ItemDTO {
        // sp_estimate_item
        @Schema(description = "견적 항목 ID")
        private Long estimateItemId;
        @Schema(description = "PCB 부품 doc_id")
        private String pcbPartDocId;
        @Schema(description = "수량")
        private Integer qty;
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
        @Schema(description = "공급자명")
        private String offerName;

        // sp_partner_order_item (null 가능 = 미발주)
        @Schema(description = "발주 항목 ID")
        private Long orderItemId;
        @Schema(description = "발주 선택 가격 (JSON)")
        private Object orderSelectedPrice;
        @Schema(description = "발주 상태")
        private String orderStatus;
        @Schema(description = "발주 메모")
        private String orderMemo;
        @Schema(description = "발주 Date Code")
        private String orderDateCode;
        @Schema(description = "발주 납기일")
        private Date orderDeliveryDate;
        @Schema(description = "발주 작성일")
        private Date orderWriteDate;
        @Schema(description = "발주 수정일")
        private Date orderModifyDate;

        public ItemDTO() {}

        public ItemDTO(Long estimateItemId, String pcbPartDocId, Integer qty, String analysisMeta, String selectedPrice,
                       String partName, String description, String manufacturerName, String partsPackaging, String size,
                       String offerName,
                       Long orderItemId, String orderSelectedPrice, String orderStatus,
                       String orderMemo, String orderDateCode, Date orderDeliveryDate,
                       Date orderWriteDate, Date orderModifyDate) {
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
            this.offerName = offerName;
            this.orderItemId = orderItemId;
            this.orderSelectedPrice = parseJson(orderSelectedPrice);
            this.orderStatus = orderStatus;
            this.orderMemo = orderMemo;
            this.orderDateCode = orderDateCode;
            this.orderDeliveryDate = orderDeliveryDate;
            this.orderWriteDate = orderWriteDate;
            this.orderModifyDate = orderModifyDate;
        }

        public Long getEstimateItemId() { return estimateItemId; }
        public void setEstimateItemId(Long estimateItemId) { this.estimateItemId = estimateItemId; }
        public String getPcbPartDocId() { return pcbPartDocId; }
        public void setPcbPartDocId(String pcbPartDocId) { this.pcbPartDocId = pcbPartDocId; }
        public Integer getQty() { return qty; }
        public void setQty(Integer qty) { this.qty = qty; }
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
        public String getOfferName() { return offerName; }
        public void setOfferName(String offerName) { this.offerName = offerName; }
        public Long getOrderItemId() { return orderItemId; }
        public void setOrderItemId(Long orderItemId) { this.orderItemId = orderItemId; }
        public Object getOrderSelectedPrice() { return orderSelectedPrice; }
        public void setOrderSelectedPrice(Object orderSelectedPrice) { this.orderSelectedPrice = orderSelectedPrice; }
        public String getOrderStatus() { return orderStatus; }
        public void setOrderStatus(String orderStatus) { this.orderStatus = orderStatus; }
        public String getOrderMemo() { return orderMemo; }
        public void setOrderMemo(String orderMemo) { this.orderMemo = orderMemo; }
        public String getOrderDateCode() { return orderDateCode; }
        public void setOrderDateCode(String orderDateCode) { this.orderDateCode = orderDateCode; }
        public Date getOrderDeliveryDate() { return orderDeliveryDate; }
        public void setOrderDeliveryDate(Date orderDeliveryDate) { this.orderDeliveryDate = orderDeliveryDate; }
        public Date getOrderWriteDate() { return orderWriteDate; }
        public void setOrderWriteDate(Date orderWriteDate) { this.orderWriteDate = orderWriteDate; }
        public Date getOrderModifyDate() { return orderModifyDate; }
        public void setOrderModifyDate(Date orderModifyDate) { this.orderModifyDate = orderModifyDate; }
    }

    // === getter / setter ===

    public Long getEstimateDocumentId() { return estimateDocumentId; }
    public void setEstimateDocumentId(Long estimateDocumentId) { this.estimateDocumentId = estimateDocumentId; }
    public String getItId() { return itId; }
    public void setItId(String itId) { this.itId = itId; }
    public String getItName() { return itName; }
    public void setItName(String itName) { this.itName = itName; }
    public String getEstimateStatus() { return estimateStatus; }
    public void setEstimateStatus(String estimateStatus) { this.estimateStatus = estimateStatus; }
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
    public List<ItemDTO> getItems() { return items; }
    public void setItems(List<ItemDTO> items) { this.items = items; }

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
