package kr.co.samplepcb.xpse.pojo;

import io.swagger.v3.oas.annotations.media.Schema;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import java.util.Date;

@Schema(description = "협력사 주문 상세")
public class SpPartnerOrderDetailDTO {

    private static final ObjectMapper objectMapper = JsonMapper.builder().build();

    @Schema(description = "주문 ID")
    private long id;
    @Schema(description = "아이템 ID")
    private String itId;
    @Schema(description = "파트너 회원번호")
    private int partnerMbNo;
    @Schema(description = "메타 아이템 (JSON)")
    private Object metaItem;
    @Schema(description = "주문 상태")
    private String status;
    @Schema(description = "파트너 선정 여부")
    private int isSelectPartner;
    @Schema(description = "가격")
    private Integer price;
    @Schema(description = "포워더")
    private String forwarder;
    @Schema(description = "배송일")
    private Date shipping;
    @Schema(description = "추적번호")
    private String tracking;
    @Schema(description = "견적 파일 제목")
    private String estimateFile1Subj;
    @Schema(description = "견적 파일")
    private String estimateFile1;
    @Schema(description = "메모")
    private String memo;
    @Schema(description = "작성일")
    private Date writeDate;
    @Schema(description = "수정일")
    private Date modifyDate;

    @Schema(description = "아이템명")
    private String itemName;
    @Schema(description = "제조사")
    private String itemMaker;
    @Schema(description = "모델명")
    private String itemModel;
    @Schema(description = "브랜드")
    private String itemBrand;
    @Schema(description = "아이템 대표 이미지")
    private String itemImg1;
    @Schema(description = "아이템 상세")
    private Object itemBasic;
    @Schema(description = "아이템 상세 설명")
    private Object itemExplan;

    @Schema(description = "파트너 회원 ID")
    private String partnerMbId;
    @Schema(description = "파트너 회원명")
    private String partnerName;
    @Schema(description = "파트너 닉네임")
    private String partnerNick;
    @Schema(description = "파트너 이메일")
    private String partnerEmail;
    @Schema(description = "파트너 휴대폰번호")
    private String partnerHp;

    public SpPartnerOrderDetailDTO() {}

    public SpPartnerOrderDetailDTO(long id, String itId, int partnerMbNo, String metaItem,
                                    String status, int isSelectPartner, Integer price,
                                    String forwarder, Date shipping, String tracking,
                                    String estimateFile1Subj, String estimateFile1,
                                    String memo, Date writeDate, Date modifyDate,
                                    String itemName, String itemMaker, String itemModel,
                                    String itemBrand, String itemImg1, String itemBasic,
                                    String itemExplan,
                                    String partnerMbId, String partnerName, String partnerNick,
                                    String partnerEmail, String partnerHp) {
        this.id = id;
        this.itId = itId;
        this.partnerMbNo = partnerMbNo;
        this.metaItem = parseJson(metaItem);
        this.status = status;
        this.isSelectPartner = isSelectPartner;
        this.price = price;
        this.forwarder = forwarder;
        this.shipping = shipping;
        this.tracking = tracking;
        this.estimateFile1Subj = estimateFile1Subj;
        this.estimateFile1 = estimateFile1;
        this.memo = memo;
        this.writeDate = writeDate;
        this.modifyDate = modifyDate;
        this.itemName = itemName;
        this.itemMaker = itemMaker;
        this.itemModel = itemModel;
        this.itemBrand = itemBrand;
        this.itemImg1 = itemImg1;
        this.itemBasic = parseJson(itemBasic);
        this.itemExplan = parseJson(itemExplan);
        this.partnerMbId = partnerMbId;
        this.partnerName = partnerName;
        this.partnerNick = partnerNick;
        this.partnerEmail = partnerEmail;
        this.partnerHp = partnerHp;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getItId() { return itId; }
    public void setItId(String itId) { this.itId = itId; }
    public int getPartnerMbNo() { return partnerMbNo; }
    public void setPartnerMbNo(int partnerMbNo) { this.partnerMbNo = partnerMbNo; }
    public Object getMetaItem() { return metaItem; }
    public void setMetaItem(Object metaItem) { this.metaItem = metaItem; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public int getIsSelectPartner() { return isSelectPartner; }
    public void setIsSelectPartner(int isSelectPartner) { this.isSelectPartner = isSelectPartner; }
    public Integer getPrice() { return price; }
    public void setPrice(Integer price) { this.price = price; }
    public String getForwarder() { return forwarder; }
    public void setForwarder(String forwarder) { this.forwarder = forwarder; }
    public Date getShipping() { return shipping; }
    public void setShipping(Date shipping) { this.shipping = shipping; }
    public String getTracking() { return tracking; }
    public void setTracking(String tracking) { this.tracking = tracking; }
    public String getEstimateFile1Subj() { return estimateFile1Subj; }
    public void setEstimateFile1Subj(String estimateFile1Subj) { this.estimateFile1Subj = estimateFile1Subj; }
    public String getEstimateFile1() { return estimateFile1; }
    public void setEstimateFile1(String estimateFile1) { this.estimateFile1 = estimateFile1; }
    public String getMemo() { return memo; }
    public void setMemo(String memo) { this.memo = memo; }
    public Date getWriteDate() { return writeDate; }
    public void setWriteDate(Date writeDate) { this.writeDate = writeDate; }
    public Date getModifyDate() { return modifyDate; }
    public void setModifyDate(Date modifyDate) { this.modifyDate = modifyDate; }
    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }
    public String getItemMaker() { return itemMaker; }
    public void setItemMaker(String itemMaker) { this.itemMaker = itemMaker; }
    public String getItemModel() { return itemModel; }
    public void setItemModel(String itemModel) { this.itemModel = itemModel; }
    public String getItemBrand() { return itemBrand; }
    public void setItemBrand(String itemBrand) { this.itemBrand = itemBrand; }
    public String getItemImg1() { return itemImg1; }
    public void setItemImg1(String itemImg1) { this.itemImg1 = itemImg1; }
    public Object getItemBasic() { return itemBasic; }
    public void setItemBasic(Object itemBasic) { this.itemBasic = itemBasic; }
    public Object getItemExplan() { return itemExplan; }
    public void setItemExplan(Object itemExplan) { this.itemExplan = itemExplan; }
    public String getPartnerMbId() { return partnerMbId; }
    public void setPartnerMbId(String partnerMbId) { this.partnerMbId = partnerMbId; }
    public String getPartnerName() { return partnerName; }
    public void setPartnerName(String partnerName) { this.partnerName = partnerName; }
    public String getPartnerNick() { return partnerNick; }
    public void setPartnerNick(String partnerNick) { this.partnerNick = partnerNick; }
    public String getPartnerEmail() { return partnerEmail; }
    public void setPartnerEmail(String partnerEmail) { this.partnerEmail = partnerEmail; }
    public String getPartnerHp() { return partnerHp; }
    public void setPartnerHp(String partnerHp) { this.partnerHp = partnerHp; }

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
