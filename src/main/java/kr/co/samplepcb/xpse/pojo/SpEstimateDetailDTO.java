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

    @Schema(description = "회원 이름")
    private String mbName;
    @Schema(description = "회원 이메일")
    private String mbEmail;
    @Schema(description = "회원 전화번호")
    private String mbTel;
    @Schema(description = "회원 휴대폰번호")
    private String mbHp;

    @Schema(description = "주문 여부")
    private boolean ordered;

    @Schema(description = "견적 항목 리스트")
    private List<EstimateItemDTO> items;
    @Schema(description = "첨부파일 리스트")
    private List<FileDTO> files;

    @Schema(description = "견적 항목")
    public static class EstimateItemDTO extends SpEstimateItemBaseDTO {
        @Schema(description = "분석 메타 (JSON)")
        private Object analysisMeta;
        @Schema(description = "선택된 가격 (JSON)")
        private Object selectedPrice;
        @Schema(description = "주문 확정 가격 (JSON)")
        private Object confirmedPrice;
        @Schema(description = "항목 마진율")
        private Integer itemMarginRate;
        @Schema(description = "선택된 협력사 견적 항목 상세")
        private SelectedPartnerEstimateItemDTO selectedPartnerEstimateItemDetail;
        @Schema(description = "작성일")
        private Date writeDate;
        @Schema(description = "수정일")
        private Date modifyDate;
        @Schema(description = "PCB 부품 정보")
        private PcbPartDTO pcbPart;

        public Object getAnalysisMeta() { return analysisMeta; }
        public void setAnalysisMeta(Object analysisMeta) { this.analysisMeta = analysisMeta; }
        public Object getSelectedPrice() { return selectedPrice; }
        public void setSelectedPrice(Object selectedPrice) { this.selectedPrice = selectedPrice; }
        public Object getConfirmedPrice() { return confirmedPrice; }
        public void setConfirmedPrice(Object confirmedPrice) { this.confirmedPrice = confirmedPrice; }
        public Integer getItemMarginRate() { return itemMarginRate; }
        public void setItemMarginRate(Integer itemMarginRate) { this.itemMarginRate = itemMarginRate; }
        public SelectedPartnerEstimateItemDTO getSelectedPartnerEstimateItemDetail() { return selectedPartnerEstimateItemDetail; }
        public void setSelectedPartnerEstimateItemDetail(SelectedPartnerEstimateItemDTO selectedPartnerEstimateItemDetail) { this.selectedPartnerEstimateItemDetail = selectedPartnerEstimateItemDetail; }
        public Date getWriteDate() { return writeDate; }
        public void setWriteDate(Date writeDate) { this.writeDate = writeDate; }
        public Date getModifyDate() { return modifyDate; }
        public void setModifyDate(Date modifyDate) { this.modifyDate = modifyDate; }
        public PcbPartDTO getPcbPart() { return pcbPart; }
        public void setPcbPart(PcbPartDTO pcbPart) { this.pcbPart = pcbPart; }
    }

    @Schema(description = "선택된 협력사 견적 항목 상세")
    public static class SelectedPartnerEstimateItemDTO {
        @Schema(description = "항목 ID")
        private Long id;
        @Schema(description = "협력사 회원번호")
        private int mbNo;
        @Schema(description = "협력사 선택 가격 (JSON)")
        private Object selectedPrice;
        @Schema(description = "상태")
        private String status;
        @Schema(description = "메모")
        private String memo;
        @Schema(description = "Date Code")
        private String dateCode;
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
        public Object getSelectedPrice() { return selectedPrice; }
        public void setSelectedPrice(Object selectedPrice) { this.selectedPrice = selectedPrice; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getMemo() { return memo; }
        public void setMemo(String memo) { this.memo = memo; }
        public String getDateCode() { return dateCode; }
        public void setDateCode(String dateCode) { this.dateCode = dateCode; }
        public Date getDeliveryDate() { return deliveryDate; }
        public void setDeliveryDate(Date deliveryDate) { this.deliveryDate = deliveryDate; }
        public Date getWriteDate() { return writeDate; }
        public void setWriteDate(Date writeDate) { this.writeDate = writeDate; }
        public Date getModifyDate() { return modifyDate; }
        public void setModifyDate(Date modifyDate) { this.modifyDate = modifyDate; }
    }

    @Schema(description = "PCB 부품 정보")
    public static class PcbPartDTO {
        @Schema(description = "부품 PK")
        private Long id;
        @Schema(description = "문서 ID")
        private String docId;
        @Schema(description = "부품명")
        private String partName;
        @Schema(description = "설명")
        private String description;
        @Schema(description = "제조사명")
        private String manufacturerName;
        @Schema(description = "부품 패키징")
        private String partsPackaging;
        @Schema(description = "패키징 상세")
        private String packaging;
        @Schema(description = "최소주문수량")
        private Integer moq;
        @Schema(description = "가격")
        private Integer price;
        @Schema(description = "공급업체명")
        private String offerName;
        @Schema(description = "대분류")
        private String largeCategory;
        @Schema(description = "중분류")
        private String mediumCategory;
        @Schema(description = "소분류")
        private String smallCategory;
        @Schema(description = "서비스 타입")
        private String serviceType;
        @Schema(description = "서브 서비스 타입")
        private String subServiceType;
        @Schema(description = "메모")
        private String memo;
        @Schema(description = "Date Code")
        private String dateCode;
        @Schema(description = "회원 ID")
        private String memberId;
        @Schema(description = "담당자 전화번호")
        private String managerPhoneNumber;
        @Schema(description = "담당자명")
        private String managerName;
        @Schema(description = "담당자 이메일")
        private String managerEmail;
        @Schema(description = "내용")
        private String contents;
        @Schema(description = "상태")
        private Integer status;
        @Schema(description = "와트")
        private String watt;
        @Schema(description = "허용오차")
        private String tolerance;
        @Schema(description = "저항")
        private String ohm;
        @Schema(description = "콘덴서")
        private String condenser;
        @Schema(description = "전압")
        private String voltage;
        @Schema(description = "온도")
        private String temperature;
        @Schema(description = "사이즈")
        private String size;
        @Schema(description = "전류")
        private String currentVal;
        @Schema(description = "인덕터")
        private String inductor;
        @Schema(description = "제품명")
        private String productName;
        @Schema(description = "사진 URL")
        private String photoUrl;
        @Schema(description = "데이터시트 URL")
        private String datasheetUrl;
        @Schema(description = "작성일")
        private Date writeDate;
        @Schema(description = "최종 수정일")
        private Date lastModifiedDate;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
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
        public String getPackaging() { return packaging; }
        public void setPackaging(String packaging) { this.packaging = packaging; }
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
        public String getServiceType() { return serviceType; }
        public void setServiceType(String serviceType) { this.serviceType = serviceType; }
        public String getSubServiceType() { return subServiceType; }
        public void setSubServiceType(String subServiceType) { this.subServiceType = subServiceType; }
        public String getMemo() { return memo; }
        public void setMemo(String memo) { this.memo = memo; }
        public String getDateCode() { return dateCode; }
        public void setDateCode(String dateCode) { this.dateCode = dateCode; }
        public String getMemberId() { return memberId; }
        public void setMemberId(String memberId) { this.memberId = memberId; }
        public String getManagerPhoneNumber() { return managerPhoneNumber; }
        public void setManagerPhoneNumber(String managerPhoneNumber) { this.managerPhoneNumber = managerPhoneNumber; }
        public String getManagerName() { return managerName; }
        public void setManagerName(String managerName) { this.managerName = managerName; }
        public String getManagerEmail() { return managerEmail; }
        public void setManagerEmail(String managerEmail) { this.managerEmail = managerEmail; }
        public String getContents() { return contents; }
        public void setContents(String contents) { this.contents = contents; }
        public Integer getStatus() { return status; }
        public void setStatus(Integer status) { this.status = status; }
        public String getWatt() { return watt; }
        public void setWatt(String watt) { this.watt = watt; }
        public String getTolerance() { return tolerance; }
        public void setTolerance(String tolerance) { this.tolerance = tolerance; }
        public String getOhm() { return ohm; }
        public void setOhm(String ohm) { this.ohm = ohm; }
        public String getCondenser() { return condenser; }
        public void setCondenser(String condenser) { this.condenser = condenser; }
        public String getVoltage() { return voltage; }
        public void setVoltage(String voltage) { this.voltage = voltage; }
        public String getTemperature() { return temperature; }
        public void setTemperature(String temperature) { this.temperature = temperature; }
        public String getSize() { return size; }
        public void setSize(String size) { this.size = size; }
        public String getCurrentVal() { return currentVal; }
        public void setCurrentVal(String currentVal) { this.currentVal = currentVal; }
        public String getInductor() { return inductor; }
        public void setInductor(String inductor) { this.inductor = inductor; }
        public String getProductName() { return productName; }
        public void setProductName(String productName) { this.productName = productName; }
        public String getPhotoUrl() { return photoUrl; }
        public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }
        public String getDatasheetUrl() { return datasheetUrl; }
        public void setDatasheetUrl(String datasheetUrl) { this.datasheetUrl = datasheetUrl; }
        public Date getWriteDate() { return writeDate; }
        public void setWriteDate(Date writeDate) { this.writeDate = writeDate; }
        public Date getLastModifiedDate() { return lastModifiedDate; }
        public void setLastModifiedDate(Date lastModifiedDate) { this.lastModifiedDate = lastModifiedDate; }
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
    public String getMbName() { return mbName; }
    public void setMbName(String mbName) { this.mbName = mbName; }
    public String getMbEmail() { return mbEmail; }
    public void setMbEmail(String mbEmail) { this.mbEmail = mbEmail; }
    public String getMbTel() { return mbTel; }
    public void setMbTel(String mbTel) { this.mbTel = mbTel; }
    public String getMbHp() { return mbHp; }
    public void setMbHp(String mbHp) { this.mbHp = mbHp; }
    public boolean isOrdered() { return ordered; }
    public void setOrdered(boolean ordered) { this.ordered = ordered; }
    public List<EstimateItemDTO> getItems() { return items; }
    public void setItems(List<EstimateItemDTO> items) { this.items = items; }
    public List<FileDTO> getFiles() { return files; }
    public void setFiles(List<FileDTO> files) { this.files = files; }
}
