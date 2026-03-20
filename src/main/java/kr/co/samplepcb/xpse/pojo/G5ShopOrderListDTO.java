package kr.co.samplepcb.xpse.pojo;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.co.samplepcb.xpse.domain.entity.G5Member;
import kr.co.samplepcb.xpse.domain.entity.G5ShopCart;
import kr.co.samplepcb.xpse.domain.entity.G5ShopOrder;

import java.util.Date;
import java.util.List;


@Schema(description = "G5 주문 목록")
public class G5ShopOrderListDTO {

    @Schema(description = "주문 ID")
    private long odId;
    @Schema(description = "회원 ID")
    private String mbId;
    @Schema(description = "주문자명")
    private String odName;
    @Schema(description = "주문자 이메일")
    private String odEmail;
    @Schema(description = "주문자 전화번호")
    private String odTel;
    @Schema(description = "주문자 휴대폰번호")
    private String odHp;
    @Schema(description = "주문 상태")
    private String odStatus;
    @Schema(description = "결제 방법")
    private String odSettleCase;
    @Schema(description = "장바구니 금액")
    private int odCartPrice;
    @Schema(description = "입금 금액")
    private int odReceiptPrice;
    @Schema(description = "취소 금액")
    private int odCancelPrice;
    @Schema(description = "환불 금액")
    private int odRefundPrice;
    @Schema(description = "배송비")
    private int odSendCost;
    @Schema(description = "택배사")
    private String odDeliveryCompany;
    @Schema(description = "송장번호")
    private String odInvoice;
    @Schema(description = "송장 등록 시간")
    private Date odInvoiceTime;
    @Schema(description = "주문 시간")
    private Date odTime;

    @Schema(description = "아이템 ID")
    private String itId;

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

    public static G5ShopOrderListDTO from(G5ShopOrder order) {
        G5ShopOrderListDTO dto = new G5ShopOrderListDTO();
        dto.setOdId(order.getOdId());
        dto.setMbId(order.getMbId());
        dto.setOdName(order.getOdName());
        dto.setOdEmail(order.getOdEmail());
        dto.setOdTel(order.getOdTel());
        dto.setOdHp(order.getOdHp());
        dto.setOdStatus(order.getOdStatus());
        dto.setOdSettleCase(order.getOdSettleCase());
        dto.setOdCartPrice(order.getOdCartPrice());
        dto.setOdReceiptPrice(order.getOdReceiptPrice());
        dto.setOdCancelPrice(order.getOdCancelPrice());
        dto.setOdRefundPrice(order.getOdRefundPrice());
        dto.setOdSendCost(order.getOdSendCost());
        dto.setOdDeliveryCompany(order.getOdDeliveryCompany());
        dto.setOdInvoice(order.getOdInvoice());
        dto.setOdInvoiceTime(order.getOdInvoiceTime());
        dto.setOdTime(order.getOdTime());

        List<G5ShopCart> carts = order.getCarts();
        if (carts != null && !carts.isEmpty()) {
            dto.setItId(carts.get(0).getItId());
        }

        G5Member member = order.getMember();
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

    public long getOdId() { return odId; }
    public void setOdId(long odId) { this.odId = odId; }
    public String getMbId() { return mbId; }
    public void setMbId(String mbId) { this.mbId = mbId; }
    public String getOdName() { return odName; }
    public void setOdName(String odName) { this.odName = odName; }
    public String getOdEmail() { return odEmail; }
    public void setOdEmail(String odEmail) { this.odEmail = odEmail; }
    public String getOdTel() { return odTel; }
    public void setOdTel(String odTel) { this.odTel = odTel; }
    public String getOdHp() { return odHp; }
    public void setOdHp(String odHp) { this.odHp = odHp; }
    public String getOdStatus() { return odStatus; }
    public void setOdStatus(String odStatus) { this.odStatus = odStatus; }
    public String getOdSettleCase() { return odSettleCase; }
    public void setOdSettleCase(String odSettleCase) { this.odSettleCase = odSettleCase; }
    public int getOdCartPrice() { return odCartPrice; }
    public void setOdCartPrice(int odCartPrice) { this.odCartPrice = odCartPrice; }
    public int getOdReceiptPrice() { return odReceiptPrice; }
    public void setOdReceiptPrice(int odReceiptPrice) { this.odReceiptPrice = odReceiptPrice; }
    public int getOdCancelPrice() { return odCancelPrice; }
    public void setOdCancelPrice(int odCancelPrice) { this.odCancelPrice = odCancelPrice; }
    public int getOdRefundPrice() { return odRefundPrice; }
    public void setOdRefundPrice(int odRefundPrice) { this.odRefundPrice = odRefundPrice; }
    public int getOdSendCost() { return odSendCost; }
    public void setOdSendCost(int odSendCost) { this.odSendCost = odSendCost; }
    public String getOdDeliveryCompany() { return odDeliveryCompany; }
    public void setOdDeliveryCompany(String odDeliveryCompany) { this.odDeliveryCompany = odDeliveryCompany; }
    public String getOdInvoice() { return odInvoice; }
    public void setOdInvoice(String odInvoice) { this.odInvoice = odInvoice; }
    public Date getOdInvoiceTime() { return odInvoiceTime; }
    public void setOdInvoiceTime(Date odInvoiceTime) { this.odInvoiceTime = odInvoiceTime; }
    public Date getOdTime() { return odTime; }
    public void setOdTime(Date odTime) { this.odTime = odTime; }
    public String getItId() { return itId; }
    public void setItId(String itId) { this.itId = itId; }
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
