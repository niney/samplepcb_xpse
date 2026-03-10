package kr.co.samplepcb.xpse.pojo;

import kr.co.samplepcb.xpse.domain.entity.G5ShopItem;
import io.swagger.v3.oas.annotations.media.Schema;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

@Schema(description = "SP 아이템 수정 요청")
public class SpItemUpdateDTO {

    private static final ObjectMapper objectMapper = JsonMapper.builder().build();

    @Schema(description = "아이템명")
    private String itName;
    @Schema(description = "제조사")
    private String itMaker;
    @Schema(description = "모델명")
    private String itModel;
    @Schema(description = "브랜드")
    private String itBrand;
    @Schema(description = "가격")
    private Integer itPrice;
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
    @Schema(description = "기본 정보 (JSON)")
    private Object itBasic;
    @Schema(description = "상세 설명 (JSON)")
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
