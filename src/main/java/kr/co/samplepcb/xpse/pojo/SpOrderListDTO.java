package kr.co.samplepcb.xpse.pojo;

import kr.co.samplepcb.xpse.domain.entity.G5Member;
import kr.co.samplepcb.xpse.domain.entity.G5ShopCart;
import kr.co.samplepcb.xpse.domain.entity.G5ShopItem;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Date;
import java.util.List;

@Schema(description = "SP 주문 목록")
public class SpOrderListDTO {

    @Schema(description = "장바구니 ID")
    private int ctId;
    @Schema(description = "주문 ID")
    private long odId;
    @Schema(description = "회원 ID")
    private String mbId;
    @Schema(description = "아이템 ID")
    private String itId;
    @Schema(description = "아이템명")
    private String itName;
    @Schema(description = "장바구니 가격")
    private int ctPrice;
    @Schema(description = "장바구니 포인트")
    private int ctPoint;
    @Schema(description = "장바구니 상태")
    private String ctStatus;
    @Schema(description = "장바구니 옵션")
    private String ctOption;
    @Schema(description = "수량")
    private int ctQty;
    @Schema(description = "장바구니 등록 시간")
    private Date ctTime;

    @Schema(description = "제조사")
    private String itemMaker;
    @Schema(description = "모델명")
    private String itemModel;
    @Schema(description = "브랜드")
    private String itemBrand;
    @Schema(description = "아이템 가격")
    private int itemPrice;
    @Schema(description = "아이템 대표 이미지")
    private String itemImg1;
    @Schema(description = "업체명")
    private String itemCompanyName;
    @Schema(description = "담당자명")
    private String itemMemberName;
    @Schema(description = "담당자 전화번호")
    private String itemMemberTel;
    @Schema(description = "담당자 이메일")
    private String itemMemberMail;
    @Schema(description = "예상 납기일")
    private String itemEta;
    @Schema(description = "견적 상태")
    private String itemEstimateStatus;

    @Schema(description = "회원 번호")
    private int memberNo;
    @Schema(description = "회원명")
    private String memberName;
    @Schema(description = "회원 닉네임")
    private String memberNick;
    @Schema(description = "회원 이메일")
    private String memberEmail;
    @Schema(description = "회원 전화번호")
    private String memberTel;
    @Schema(description = "회원 휴대폰번호")
    private String memberHp;

    public static SpOrderListDTO from(G5ShopCart cart) {
        SpOrderListDTO dto = new SpOrderListDTO();
        dto.setCtId(cart.getCtId());
        dto.setOdId(cart.getOdId());
        dto.setMbId(cart.getMbId());
        dto.setItId(cart.getItId());
        dto.setItName(cart.getItName());
        dto.setCtPrice(cart.getCtPrice());
        dto.setCtPoint(cart.getCtPoint());
        dto.setCtStatus(cart.getCtStatus());
        dto.setCtOption(cart.getCtOption());
        dto.setCtQty(cart.getCtQty());
        dto.setCtTime(cart.getCtTime());

        G5ShopItem item = cart.getShopItem();
        if (item != null) {
            dto.setItemMaker(item.getItMaker());
            dto.setItemModel(item.getItModel());
            dto.setItemBrand(item.getItBrand());
            dto.setItemPrice(item.getItPrice());
            dto.setItemImg1(item.getItImg1());
            dto.setItemCompanyName(item.getItCompanyName());
            dto.setItemMemberName(item.getItMemberName());
            dto.setItemMemberTel(item.getItMemberTel());
            dto.setItemMemberMail(item.getItMemberMail());
            dto.setItemEta(item.getItEta());
            dto.setItemEstimateStatus(item.getIt24());
        }

        G5Member member = cart.getMember();
        if (member != null) {
            dto.setMemberNo(member.getMbNo());
            dto.setMemberName(member.getMbName());
            dto.setMemberNick(member.getMbNick());
            dto.setMemberEmail(member.getMbEmail());
            dto.setMemberTel(member.getMbTel());
            dto.setMemberHp(member.getMbHp());
        }

        return dto;
    }

    public int getCtId() { return ctId; }
    public void setCtId(int ctId) { this.ctId = ctId; }
    public long getOdId() { return odId; }
    public void setOdId(long odId) { this.odId = odId; }
    public String getMbId() { return mbId; }
    public void setMbId(String mbId) { this.mbId = mbId; }
    public String getItId() { return itId; }
    public void setItId(String itId) { this.itId = itId; }
    public String getItName() { return itName; }
    public void setItName(String itName) { this.itName = itName; }
    public int getCtPrice() { return ctPrice; }
    public void setCtPrice(int ctPrice) { this.ctPrice = ctPrice; }
    public int getCtPoint() { return ctPoint; }
    public void setCtPoint(int ctPoint) { this.ctPoint = ctPoint; }
    public String getCtStatus() { return ctStatus; }
    public void setCtStatus(String ctStatus) { this.ctStatus = ctStatus; }
    public String getCtOption() { return ctOption; }
    public void setCtOption(String ctOption) { this.ctOption = ctOption; }
    public int getCtQty() { return ctQty; }
    public void setCtQty(int ctQty) { this.ctQty = ctQty; }
    public Date getCtTime() { return ctTime; }
    public void setCtTime(Date ctTime) { this.ctTime = ctTime; }
    public String getItemMaker() { return itemMaker; }
    public void setItemMaker(String itemMaker) { this.itemMaker = itemMaker; }
    public String getItemModel() { return itemModel; }
    public void setItemModel(String itemModel) { this.itemModel = itemModel; }
    public String getItemBrand() { return itemBrand; }
    public void setItemBrand(String itemBrand) { this.itemBrand = itemBrand; }
    public int getItemPrice() { return itemPrice; }
    public void setItemPrice(int itemPrice) { this.itemPrice = itemPrice; }
    public String getItemImg1() { return itemImg1; }
    public void setItemImg1(String itemImg1) { this.itemImg1 = itemImg1; }
    public String getItemCompanyName() { return itemCompanyName; }
    public void setItemCompanyName(String itemCompanyName) { this.itemCompanyName = itemCompanyName; }
    public String getItemMemberName() { return itemMemberName; }
    public void setItemMemberName(String itemMemberName) { this.itemMemberName = itemMemberName; }
    public String getItemMemberTel() { return itemMemberTel; }
    public void setItemMemberTel(String itemMemberTel) { this.itemMemberTel = itemMemberTel; }
    public String getItemMemberMail() { return itemMemberMail; }
    public void setItemMemberMail(String itemMemberMail) { this.itemMemberMail = itemMemberMail; }
    public String getItemEta() { return itemEta; }
    public void setItemEta(String itemEta) { this.itemEta = itemEta; }
    public String getItemEstimateStatus() { return itemEstimateStatus; }
    public void setItemEstimateStatus(String itemEstimateStatus) { this.itemEstimateStatus = itemEstimateStatus; }
    public int getMemberNo() { return memberNo; }
    public void setMemberNo(int memberNo) { this.memberNo = memberNo; }
    public String getMemberName() { return memberName; }
    public void setMemberName(String memberName) { this.memberName = memberName; }
    public String getMemberNick() { return memberNick; }
    public void setMemberNick(String memberNick) { this.memberNick = memberNick; }
    public String getMemberEmail() { return memberEmail; }
    public void setMemberEmail(String memberEmail) { this.memberEmail = memberEmail; }
    public String getMemberTel() { return memberTel; }
    public void setMemberTel(String memberTel) { this.memberTel = memberTel; }
    public String getMemberHp() { return memberHp; }
    public void setMemberHp(String memberHp) { this.memberHp = memberHp; }
}
