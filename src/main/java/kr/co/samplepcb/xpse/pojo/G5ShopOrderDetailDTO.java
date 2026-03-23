package kr.co.samplepcb.xpse.pojo;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.co.samplepcb.xpse.domain.entity.G5Member;
import kr.co.samplepcb.xpse.domain.entity.G5ShopCart;
import kr.co.samplepcb.xpse.domain.entity.G5ShopOrder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Schema(description = "G5 주문 상세")
public class G5ShopOrderDetailDTO {

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
    @Schema(description = "주문자 우편번호1")
    private String odZip1;
    @Schema(description = "주문자 우편번호2")
    private String odZip2;
    @Schema(description = "주문자 주소1")
    private String odAddr1;
    @Schema(description = "주문자 주소2")
    private String odAddr2;
    @Schema(description = "주문자 주소3")
    private String odAddr3;
    @Schema(description = "주문자 지번주소")
    private String odAddrJibeon;

    @Schema(description = "입금자명")
    private String odDepositName;

    @Schema(description = "배송지 수령인명")
    private String odBName;
    @Schema(description = "배송지 전화번호")
    private String odBTel;
    @Schema(description = "배송지 휴대폰번호")
    private String odBHp;
    @Schema(description = "배송지 우편번호1")
    private String odBZip1;
    @Schema(description = "배송지 우편번호2")
    private String odBZip2;
    @Schema(description = "배송지 주소1")
    private String odBAddr1;
    @Schema(description = "배송지 주소2")
    private String odBAddr2;
    @Schema(description = "배송지 주소3")
    private String odBAddr3;
    @Schema(description = "배송지 지번주소")
    private String odBAddrJibeon;

    @Schema(description = "주문 메모")
    private String odMemo;
    @Schema(description = "관리자 메모")
    private String odShopMemo;

    @Schema(description = "장바구니 수량")
    private int odCartCount;
    @Schema(description = "장바구니 금액")
    private int odCartPrice;
    @Schema(description = "장바구니 쿠폰")
    private int odCartCoupon;
    @Schema(description = "배송비")
    private int odSendCost;
    @Schema(description = "배송비2")
    private int odSendCost2;
    @Schema(description = "배송 쿠폰")
    private int odSendCoupon;
    @Schema(description = "입금 금액")
    private int odReceiptPrice;
    @Schema(description = "취소 금액")
    private int odCancelPrice;
    @Schema(description = "사용 포인트")
    private int odReceiptPoint;
    @Schema(description = "환불 금액")
    private int odRefundPrice;
    @Schema(description = "입금 계좌")
    private String odBankAccount;
    @Schema(description = "입금 시간")
    private Date odReceiptTime;
    @Schema(description = "쿠폰")
    private int odCoupon;
    @Schema(description = "미수금")
    private int odMisu;

    @Schema(description = "주문 상태")
    private String odStatus;
    @Schema(description = "결제 방법")
    private String odSettleCase;
    @Schema(description = "PG사")
    private String odPg;
    @Schema(description = "거래번호")
    private String odTno;
    @Schema(description = "승인번호")
    private String odAppNo;
    @Schema(description = "희망 배송일")
    private Date odHopeDate;

    @Schema(description = "택배사")
    private String odDeliveryCompany;
    @Schema(description = "송장번호")
    private String odInvoice;
    @Schema(description = "송장 등록 시간")
    private Date odInvoiceTime;

    @Schema(description = "주문 시간")
    private Date odTime;
    @Schema(description = "수정 이력")
    private String odModHistory;

    @Schema(description = "장바구니 항목 목록")
    private List<CartItem> cartItems;

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

    @Schema(description = "장바구니 항목")
    public static class CartItem {
        @Schema(description = "장바구니 ID")
        private int ctId;
        @Schema(description = "아이템 ID")
        private String itId;
        @Schema(description = "아이템명")
        private String itName;
        @Schema(description = "상태")
        private String ctStatus;
        @Schema(description = "가격")
        private int ctPrice;
        @Schema(description = "수량")
        private int ctQty;
        @Schema(description = "옵션")
        private String ctOption;
        @Schema(description = "포인트")
        private int ctPoint;
        @Schema(description = "배송비")
        private int ctSendCost;

        public static CartItem from(G5ShopCart cart) {
            CartItem item = new CartItem();
            item.setCtId(cart.getCtId());
            item.setItId(cart.getItId());
            item.setItName(cart.getItName());
            item.setCtStatus(cart.getCtStatus());
            item.setCtPrice(cart.getCtPrice());
            item.setCtQty(cart.getCtQty());
            item.setCtOption(cart.getCtOption());
            item.setCtPoint(cart.getCtPoint());
            item.setCtSendCost(cart.getCtSendCost());
            return item;
        }

        public int getCtId() { return ctId; }
        public void setCtId(int ctId) { this.ctId = ctId; }
        public String getItId() { return itId; }
        public void setItId(String itId) { this.itId = itId; }
        public String getItName() { return itName; }
        public void setItName(String itName) { this.itName = itName; }
        public String getCtStatus() { return ctStatus; }
        public void setCtStatus(String ctStatus) { this.ctStatus = ctStatus; }
        public int getCtPrice() { return ctPrice; }
        public void setCtPrice(int ctPrice) { this.ctPrice = ctPrice; }
        public int getCtQty() { return ctQty; }
        public void setCtQty(int ctQty) { this.ctQty = ctQty; }
        public String getCtOption() { return ctOption; }
        public void setCtOption(String ctOption) { this.ctOption = ctOption; }
        public int getCtPoint() { return ctPoint; }
        public void setCtPoint(int ctPoint) { this.ctPoint = ctPoint; }
        public int getCtSendCost() { return ctSendCost; }
        public void setCtSendCost(int ctSendCost) { this.ctSendCost = ctSendCost; }
    }

    public static G5ShopOrderDetailDTO from(G5ShopOrder order) {
        G5ShopOrderDetailDTO dto = new G5ShopOrderDetailDTO();
        dto.setOdId(order.getOdId());
        dto.setMbId(order.getMbId());
        dto.setOdName(order.getOdName());
        dto.setOdEmail(order.getOdEmail());
        dto.setOdTel(order.getOdTel());
        dto.setOdHp(order.getOdHp());
        dto.setOdZip1(order.getOdZip1());
        dto.setOdZip2(order.getOdZip2());
        dto.setOdAddr1(order.getOdAddr1());
        dto.setOdAddr2(order.getOdAddr2());
        dto.setOdAddr3(order.getOdAddr3());
        dto.setOdAddrJibeon(order.getOdAddrJibeon());

        dto.setOdDepositName(order.getOdDepositName());

        dto.setOdBName(order.getOdBName());
        dto.setOdBTel(order.getOdBTel());
        dto.setOdBHp(order.getOdBHp());
        dto.setOdBZip1(order.getOdBZip1());
        dto.setOdBZip2(order.getOdBZip2());
        dto.setOdBAddr1(order.getOdBAddr1());
        dto.setOdBAddr2(order.getOdBAddr2());
        dto.setOdBAddr3(order.getOdBAddr3());
        dto.setOdBAddrJibeon(order.getOdBAddrJibeon());

        dto.setOdMemo(order.getOdMemo());
        dto.setOdShopMemo(order.getOdShopMemo());

        dto.setOdCartCount(order.getOdCartCount());
        dto.setOdCartPrice(order.getOdCartPrice());
        dto.setOdCartCoupon(order.getOdCartCoupon());
        dto.setOdSendCost(order.getOdSendCost());
        dto.setOdSendCost2(order.getOdSendCost2());
        dto.setOdSendCoupon(order.getOdSendCoupon());
        dto.setOdReceiptPrice(order.getOdReceiptPrice());
        dto.setOdCancelPrice(order.getOdCancelPrice());
        dto.setOdReceiptPoint(order.getOdReceiptPoint());
        dto.setOdRefundPrice(order.getOdRefundPrice());
        dto.setOdBankAccount(order.getOdBankAccount());
        dto.setOdReceiptTime(order.getOdReceiptTime());
        dto.setOdCoupon(order.getOdCoupon());
        dto.setOdMisu(order.getOdMisu());

        dto.setOdStatus(order.getOdStatus());
        dto.setOdSettleCase(order.getOdSettleCase());
        dto.setOdPg(order.getOdPg());
        dto.setOdTno(order.getOdTno());
        dto.setOdAppNo(order.getOdAppNo());
        dto.setOdHopeDate(order.getOdHopeDate());

        dto.setOdDeliveryCompany(order.getOdDeliveryCompany());
        dto.setOdInvoice(order.getOdInvoice());
        dto.setOdInvoiceTime(order.getOdInvoiceTime());

        dto.setOdTime(order.getOdTime());
        dto.setOdModHistory(order.getOdModHistory());

        List<G5ShopCart> carts = order.getCarts();
        if (carts != null && !carts.isEmpty()) {
            dto.setCartItems(carts.stream().map(CartItem::from).toList());
        } else {
            dto.setCartItems(new ArrayList<>());
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
    public String getOdZip1() { return odZip1; }
    public void setOdZip1(String odZip1) { this.odZip1 = odZip1; }
    public String getOdZip2() { return odZip2; }
    public void setOdZip2(String odZip2) { this.odZip2 = odZip2; }
    public String getOdAddr1() { return odAddr1; }
    public void setOdAddr1(String odAddr1) { this.odAddr1 = odAddr1; }
    public String getOdAddr2() { return odAddr2; }
    public void setOdAddr2(String odAddr2) { this.odAddr2 = odAddr2; }
    public String getOdAddr3() { return odAddr3; }
    public void setOdAddr3(String odAddr3) { this.odAddr3 = odAddr3; }
    public String getOdAddrJibeon() { return odAddrJibeon; }
    public void setOdAddrJibeon(String odAddrJibeon) { this.odAddrJibeon = odAddrJibeon; }
    public String getOdDepositName() { return odDepositName; }
    public void setOdDepositName(String odDepositName) { this.odDepositName = odDepositName; }
    public String getOdBName() { return odBName; }
    public void setOdBName(String odBName) { this.odBName = odBName; }
    public String getOdBTel() { return odBTel; }
    public void setOdBTel(String odBTel) { this.odBTel = odBTel; }
    public String getOdBHp() { return odBHp; }
    public void setOdBHp(String odBHp) { this.odBHp = odBHp; }
    public String getOdBZip1() { return odBZip1; }
    public void setOdBZip1(String odBZip1) { this.odBZip1 = odBZip1; }
    public String getOdBZip2() { return odBZip2; }
    public void setOdBZip2(String odBZip2) { this.odBZip2 = odBZip2; }
    public String getOdBAddr1() { return odBAddr1; }
    public void setOdBAddr1(String odBAddr1) { this.odBAddr1 = odBAddr1; }
    public String getOdBAddr2() { return odBAddr2; }
    public void setOdBAddr2(String odBAddr2) { this.odBAddr2 = odBAddr2; }
    public String getOdBAddr3() { return odBAddr3; }
    public void setOdBAddr3(String odBAddr3) { this.odBAddr3 = odBAddr3; }
    public String getOdBAddrJibeon() { return odBAddrJibeon; }
    public void setOdBAddrJibeon(String odBAddrJibeon) { this.odBAddrJibeon = odBAddrJibeon; }
    public String getOdMemo() { return odMemo; }
    public void setOdMemo(String odMemo) { this.odMemo = odMemo; }
    public String getOdShopMemo() { return odShopMemo; }
    public void setOdShopMemo(String odShopMemo) { this.odShopMemo = odShopMemo; }
    public int getOdCartCount() { return odCartCount; }
    public void setOdCartCount(int odCartCount) { this.odCartCount = odCartCount; }
    public int getOdCartPrice() { return odCartPrice; }
    public void setOdCartPrice(int odCartPrice) { this.odCartPrice = odCartPrice; }
    public int getOdCartCoupon() { return odCartCoupon; }
    public void setOdCartCoupon(int odCartCoupon) { this.odCartCoupon = odCartCoupon; }
    public int getOdSendCost() { return odSendCost; }
    public void setOdSendCost(int odSendCost) { this.odSendCost = odSendCost; }
    public int getOdSendCost2() { return odSendCost2; }
    public void setOdSendCost2(int odSendCost2) { this.odSendCost2 = odSendCost2; }
    public int getOdSendCoupon() { return odSendCoupon; }
    public void setOdSendCoupon(int odSendCoupon) { this.odSendCoupon = odSendCoupon; }
    public int getOdReceiptPrice() { return odReceiptPrice; }
    public void setOdReceiptPrice(int odReceiptPrice) { this.odReceiptPrice = odReceiptPrice; }
    public int getOdCancelPrice() { return odCancelPrice; }
    public void setOdCancelPrice(int odCancelPrice) { this.odCancelPrice = odCancelPrice; }
    public int getOdReceiptPoint() { return odReceiptPoint; }
    public void setOdReceiptPoint(int odReceiptPoint) { this.odReceiptPoint = odReceiptPoint; }
    public int getOdRefundPrice() { return odRefundPrice; }
    public void setOdRefundPrice(int odRefundPrice) { this.odRefundPrice = odRefundPrice; }
    public String getOdBankAccount() { return odBankAccount; }
    public void setOdBankAccount(String odBankAccount) { this.odBankAccount = odBankAccount; }
    public Date getOdReceiptTime() { return odReceiptTime; }
    public void setOdReceiptTime(Date odReceiptTime) { this.odReceiptTime = odReceiptTime; }
    public int getOdCoupon() { return odCoupon; }
    public void setOdCoupon(int odCoupon) { this.odCoupon = odCoupon; }
    public int getOdMisu() { return odMisu; }
    public void setOdMisu(int odMisu) { this.odMisu = odMisu; }
    public String getOdStatus() { return odStatus; }
    public void setOdStatus(String odStatus) { this.odStatus = odStatus; }
    public String getOdSettleCase() { return odSettleCase; }
    public void setOdSettleCase(String odSettleCase) { this.odSettleCase = odSettleCase; }
    public String getOdPg() { return odPg; }
    public void setOdPg(String odPg) { this.odPg = odPg; }
    public String getOdTno() { return odTno; }
    public void setOdTno(String odTno) { this.odTno = odTno; }
    public String getOdAppNo() { return odAppNo; }
    public void setOdAppNo(String odAppNo) { this.odAppNo = odAppNo; }
    public Date getOdHopeDate() { return odHopeDate; }
    public void setOdHopeDate(Date odHopeDate) { this.odHopeDate = odHopeDate; }
    public String getOdDeliveryCompany() { return odDeliveryCompany; }
    public void setOdDeliveryCompany(String odDeliveryCompany) { this.odDeliveryCompany = odDeliveryCompany; }
    public String getOdInvoice() { return odInvoice; }
    public void setOdInvoice(String odInvoice) { this.odInvoice = odInvoice; }
    public Date getOdInvoiceTime() { return odInvoiceTime; }
    public void setOdInvoiceTime(Date odInvoiceTime) { this.odInvoiceTime = odInvoiceTime; }
    public Date getOdTime() { return odTime; }
    public void setOdTime(Date odTime) { this.odTime = odTime; }
    public String getOdModHistory() { return odModHistory; }
    public void setOdModHistory(String odModHistory) { this.odModHistory = odModHistory; }
    public List<CartItem> getCartItems() { return cartItems; }
    public void setCartItems(List<CartItem> cartItems) { this.cartItems = cartItems; }
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
