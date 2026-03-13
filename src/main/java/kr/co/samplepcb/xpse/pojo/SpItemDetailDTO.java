package kr.co.samplepcb.xpse.pojo;

import kr.co.samplepcb.xpse.domain.entity.G5ShopItem;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import io.swagger.v3.oas.annotations.media.Schema;

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

        return dto;
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
}
