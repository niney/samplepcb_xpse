package kr.co.samplepcb.xpse.pojo;

import kr.co.samplepcb.xpse.domain.entity.G5ShopItem;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

public class SpItemUpdateDTO {

    private static final ObjectMapper objectMapper = JsonMapper.builder().build();

    private String itName;
    private String itMaker;
    private String itModel;
    private String itBrand;
    private Integer itPrice;
    private String itImg1;
    private String itCompanyName;
    private String itMemberName;
    private String itMemberTel;
    private String itMemberMail;
    private String itEta;
    private String itEstimateStatus;
    private Object itBasic;
    private Object itExplan;

    public void applyTo(G5ShopItem item) {
        if (itName != null) item.setItName(itName);
        if (itMaker != null) item.setItMaker(itMaker);
        if (itModel != null) item.setItModel(itModel);
        if (itBrand != null) item.setItBrand(itBrand);
        if (itPrice != null) item.setItPrice(itPrice);
        if (itImg1 != null) item.setItImg1(itImg1);
        if (itCompanyName != null) item.setItCompanyName(itCompanyName);
        if (itMemberName != null) item.setItMemberName(itMemberName);
        if (itMemberTel != null) item.setItMemberTel(itMemberTel);
        if (itMemberMail != null) item.setItMemberMail(itMemberMail);
        if (itEta != null) item.setItEta(itEta);
        if (itEstimateStatus != null) item.setIt24(itEstimateStatus);
        if (itBasic != null) item.setItBasic(toJson(itBasic));
        if (itExplan != null) item.setItExplan(toJson(itExplan));
    }

    private static String toJson(Object value) {
        if (value instanceof String) {
            return (String) value;
        }
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JacksonException e) {
            return String.valueOf(value);
        }
    }

    public String getItName() { return itName; }
    public void setItName(String itName) { this.itName = itName; }
    public String getItMaker() { return itMaker; }
    public void setItMaker(String itMaker) { this.itMaker = itMaker; }
    public String getItModel() { return itModel; }
    public void setItModel(String itModel) { this.itModel = itModel; }
    public String getItBrand() { return itBrand; }
    public void setItBrand(String itBrand) { this.itBrand = itBrand; }
    public Integer getItPrice() { return itPrice; }
    public void setItPrice(Integer itPrice) { this.itPrice = itPrice; }
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
}
