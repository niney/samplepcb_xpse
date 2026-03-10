package kr.co.samplepcb.xpse.pojo;

import kr.co.samplepcb.xpse.domain.entity.G5ShopItem;
import kr.co.samplepcb.xpse.domain.entity.SpPartnerOrder;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Schema(description = "SP 아이템 상세 정보")
public class SpItemDetailDTO {

    @Schema(description = "아이템 ID")
    private String itId;
    @Schema(description = "카테고리 ID")
    private String caId;
    @Schema(description = "아이템명")
    private String itName;
    @Schema(description = "제조사")
    private String itMaker;
    @Schema(description = "모델명")
    private String itModel;
    @Schema(description = "브랜드")
    private String itBrand;
    @Schema(description = "가격")
    private int itPrice;
    @Schema(description = "대표 이미지")
    private String itImg1;
    @Schema(description = "업체명")
    private String itCompanyName;
    @Schema(description = "담당자명")
    private String itMemberName;
    @Schema(description = "담당자 전화번호")
    private String itMemberTel;
    @Schema(description = "담당자 이메일")
    private String itMemberMail;
    @Schema(description = "예상 납기일")
    private String itEta;
    @Schema(description = "견적 상태")
    private String itEstimateStatus;
    private static final ObjectMapper objectMapper = JsonMapper.builder().build();

    @Schema(description = "기본 정보 (JSON)")
    private Object itBasic;
    @Schema(description = "상세 설명 (JSON)")
    private Object itExplan;

    @Schema(description = "파트너 주문 목록")
    private List<PartnerOrderDTO> partnerOrders;

    public static SpItemDetailDTO from(G5ShopItem item) {
        SpItemDetailDTO dto = new SpItemDetailDTO();
        dto.setItId(item.getItId());
        dto.setCaId(item.getCaId());
        dto.setItName(item.getItName());
        dto.setItMaker(item.getItMaker());
        dto.setItModel(item.getItModel());
        dto.setItBrand(item.getItBrand());
        dto.setItPrice(item.getItPrice());
        dto.setItImg1(item.getItImg1());
        dto.setItCompanyName(item.getItCompanyName());
        dto.setItMemberName(item.getItMemberName());
        dto.setItMemberTel(item.getItMemberTel());
        dto.setItMemberMail(item.getItMemberMail());
        dto.setItEta(item.getItEta());
        dto.setItEstimateStatus(item.getIt24());
        dto.setItBasic(parseJson(item.getItBasic()));
        dto.setItExplan(parseJson(item.getItExplan()));

        List<SpPartnerOrder> poList = item.getPartnerOrders();
        if (poList != null && !poList.isEmpty()) {
            List<PartnerOrderDTO> poDtos = new ArrayList<>();
            for (SpPartnerOrder po : poList) {
                poDtos.add(PartnerOrderDTO.from(po));
            }
            dto.setPartnerOrders(poDtos);
        }

        return dto;
    }

    @Schema(description = "파트너 주문 정보")
    public static class PartnerOrderDTO {
        @Schema(description = "주문 ID")
        private long id;
        @Schema(description = "파트너 회원번호")
        private int partnerMbNo;
        @Schema(description = "주문 상태")
        private String status;
        @Schema(description = "파트너 선정 여부")
        private int isSelectPartner;
        @Schema(description = "가격")
        private int price;
        @Schema(description = "포워더")
        private String forwarder;
        @Schema(description = "배송일")
        private Date shipping;
        @Schema(description = "추적번호")
        private String tracking;
        @Schema(description = "메모")
        private String memo;
        @Schema(description = "작성일")
        private Date writeDate;
        @Schema(description = "수정일")
        private Date modifyDate;

        public static PartnerOrderDTO from(SpPartnerOrder po) {
            PartnerOrderDTO dto = new PartnerOrderDTO();
            dto.setId(po.getId());
            dto.setPartnerMbNo(po.getPartnerMbNo());
            dto.setStatus(po.getStatus());
            dto.setIsSelectPartner(po.getIsSelectPartner());
            dto.setPrice(po.getPrice());
            dto.setForwarder(po.getForwarder());
            dto.setShipping(po.getShipping());
            dto.setTracking(po.getTracking());
            dto.setMemo(po.getMemo());
            dto.setWriteDate(po.getWriteDate());
            dto.setModifyDate(po.getModifyDate());
            return dto;
        }

        public long getId() { return id; }
        public void setId(long id) { this.id = id; }
        public int getPartnerMbNo() { return partnerMbNo; }
        public void setPartnerMbNo(int partnerMbNo) { this.partnerMbNo = partnerMbNo; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public int getIsSelectPartner() { return isSelectPartner; }
        public void setIsSelectPartner(int isSelectPartner) { this.isSelectPartner = isSelectPartner; }
        public int getPrice() { return price; }
        public void setPrice(int price) { this.price = price; }
        public String getForwarder() { return forwarder; }
        public void setForwarder(String forwarder) { this.forwarder = forwarder; }
        public Date getShipping() { return shipping; }
        public void setShipping(Date shipping) { this.shipping = shipping; }
        public String getTracking() { return tracking; }
        public void setTracking(String tracking) { this.tracking = tracking; }
        public String getMemo() { return memo; }
        public void setMemo(String memo) { this.memo = memo; }
        public Date getWriteDate() { return writeDate; }
        public void setWriteDate(Date writeDate) { this.writeDate = writeDate; }
        public Date getModifyDate() { return modifyDate; }
        public void setModifyDate(Date modifyDate) { this.modifyDate = modifyDate; }
    }

    public String getItId() { return itId; }
    public void setItId(String itId) { this.itId = itId; }
    public String getCaId() { return caId; }
    public void setCaId(String caId) { this.caId = caId; }
    public String getItName() { return itName; }
    public void setItName(String itName) { this.itName = itName; }
    public String getItMaker() { return itMaker; }
    public void setItMaker(String itMaker) { this.itMaker = itMaker; }
    public String getItModel() { return itModel; }
    public void setItModel(String itModel) { this.itModel = itModel; }
    public String getItBrand() { return itBrand; }
    public void setItBrand(String itBrand) { this.itBrand = itBrand; }
    public int getItPrice() { return itPrice; }
    public void setItPrice(int itPrice) { this.itPrice = itPrice; }
    public String getItImg1() { return itImg1; }
    public void setItImg1(String itImg1) { this.itImg1 = itImg1; }
    public String getItCompanyName() { return itCompanyName; }
    public void setItCompanyName(String itCompanyName) { this.itCompanyName = itCompanyName; }
    public String getItMemberName() { return itMemberName; }
    public void setItMemberName(String itMemberName) { this.itMemberName = itMemberName; }
    public String getItMemberTel() { return itMemberTel; }
    public void setItMemberTel(String itMemberTel) { this.itMemberTel = itMemberTel; }
    public String getItMemberMail() { return itMemberMail; }
    public void setItMemberMail(String itMemberMail) { this.itMemberMail = itMemberMail; }
    public String getItEta() { return itEta; }
    public void setItEta(String itEta) { this.itEta = itEta; }
    public String getItEstimateStatus() { return itEstimateStatus; }
    public void setItEstimateStatus(String itEstimateStatus) { this.itEstimateStatus = itEstimateStatus; }
    public Object getItBasic() { return itBasic; }
    public void setItBasic(Object itBasic) { this.itBasic = itBasic; }
    public Object getItExplan() { return itExplan; }
    public void setItExplan(Object itExplan) { this.itExplan = itExplan; }

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
    public List<PartnerOrderDTO> getPartnerOrders() { return partnerOrders; }
    public void setPartnerOrders(List<PartnerOrderDTO> partnerOrders) { this.partnerOrders = partnerOrders; }
}
