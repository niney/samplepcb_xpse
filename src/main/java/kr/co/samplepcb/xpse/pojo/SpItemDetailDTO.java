package kr.co.samplepcb.xpse.pojo;

import kr.co.samplepcb.xpse.domain.entity.G5ShopItem;
import kr.co.samplepcb.xpse.domain.entity.SpPartnerOrder;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SpItemDetailDTO {

    private String itId;
    private String caId;
    private String itName;
    private String itMaker;
    private String itModel;
    private String itBrand;
    private int itPrice;
    private String itImg1;
    private String itCompanyName;
    private String itMemberName;
    private String itMemberTel;
    private String itMemberMail;
    private String itEta;
    private String itEstimateStatus;
    private static final ObjectMapper objectMapper = JsonMapper.builder().build();

    private Object itBasic;
    private Object itExplan;

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

    public static class PartnerOrderDTO {
        private long id;
        private int partnerMbNo;
        private String status;
        private int isSelectPartner;
        private int price;
        private String forwarder;
        private Date shipping;
        private String tracking;
        private String memo;
        private Date writeDate;
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
