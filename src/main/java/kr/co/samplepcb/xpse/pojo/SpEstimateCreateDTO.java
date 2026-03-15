package kr.co.samplepcb.xpse.pojo;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "견적서 생성 요청 (상품 + 장바구니 + 견적서 + 견적항목 일괄 생성)")
public class SpEstimateCreateDTO {

    // ── 상품(G5ShopItem) / 장바구니(G5ShopCart) 생성용 (SpItemCreateDTO 위임) ──

    @Schema(description = "아이템 ID (미입력 시 자동 생성)")
    private String itId;

    @Schema(description = "아이템명", requiredMode = Schema.RequiredMode.REQUIRED)
    private String itName;

    @Schema(description = "판매가격")
    private int itPrice;

    @Schema(description = "재고수량")
    private int itStockQty;

    @Schema(description = "카테고리 ID")
    private String caId;

    @Schema(description = "상태 (order 또는 rfq, 기본값: order)")
    private String status;

    // ── 견적서(SpEstimateDocument) 필드 ──

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

    @Schema(description = "첨부파일 리스트")
    private List<FileDTO> files;

    // ── 견적 항목(SpEstimateItem) 리스트 ──

    @Schema(description = "견적 부품 항목 리스트")
    private List<EstimateItemDTO> items;

    @Schema(description = "견적 부품 항목")
    public static class EstimateItemDTO {

        @Schema(description = "PCB 부품 doc_id", requiredMode = Schema.RequiredMode.REQUIRED)
        private String pcbPartDocId;

        @Schema(description = "수량")
        private Integer qty;

        @Schema(description = "분석 메타 (JSON text)")
        private String analysisMeta;

        @Schema(description = "선택된 가격 (JSON text)")
        private String selectedPrice;

        public String getPcbPartDocId() { return pcbPartDocId; }
        public void setPcbPartDocId(String pcbPartDocId) { this.pcbPartDocId = pcbPartDocId; }
        public Integer getQty() { return qty; }
        public void setQty(Integer qty) { this.qty = qty; }
        public String getAnalysisMeta() { return analysisMeta; }
        public void setAnalysisMeta(String analysisMeta) { this.analysisMeta = analysisMeta; }
        public String getSelectedPrice() { return selectedPrice; }
        public void setSelectedPrice(String selectedPrice) { this.selectedPrice = selectedPrice; }
    }

    @Schema(description = "첨부파일 항목")
    public static class FileDTO {

        @Schema(description = "업로드된 파일명")
        private String uploadFileName;

        @Schema(description = "원본 파일명")
        private String originFileName;

        @Schema(description = "경로 토큰")
        private String pathToken;

        @Schema(description = "파일 크기 (bytes)")
        private long size;

        public String getUploadFileName() { return uploadFileName; }
        public void setUploadFileName(String uploadFileName) { this.uploadFileName = uploadFileName; }
        public String getOriginFileName() { return originFileName; }
        public void setOriginFileName(String originFileName) { this.originFileName = originFileName; }
        public String getPathToken() { return pathToken; }
        public void setPathToken(String pathToken) { this.pathToken = pathToken; }
        public long getSize() { return size; }
        public void setSize(long size) { this.size = size; }
    }

    /**
     * 상품/장바구니 생성에 필요한 SpItemCreateDTO로 변환.
     */
    public SpItemCreateDTO toItemCreateDTO() {
        SpItemCreateDTO dto = new SpItemCreateDTO();
        dto.setItId(itId);
        dto.setItName(itName);
        dto.setItPrice(itPrice);
        dto.setItStockQty(itStockQty);
        dto.setCaId(caId);
        dto.setStatus(status);
        return dto;
    }

    // === getter / setter ===

    public String getItId() { return itId; }
    public void setItId(String itId) { this.itId = itId; }
    public String getItName() { return itName; }
    public void setItName(String itName) { this.itName = itName; }
    public int getItPrice() { return itPrice; }
    public void setItPrice(int itPrice) { this.itPrice = itPrice; }
    public int getItStockQty() { return itStockQty; }
    public void setItStockQty(int itStockQty) { this.itStockQty = itStockQty; }
    public String getCaId() { return caId; }
    public void setCaId(String caId) { this.caId = caId; }
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
    public List<FileDTO> getFiles() { return files; }
    public void setFiles(List<FileDTO> files) { this.files = files; }
    public List<EstimateItemDTO> getItems() { return items; }
    public void setItems(List<EstimateItemDTO> items) { this.items = items; }
}
