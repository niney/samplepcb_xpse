package kr.co.samplepcb.xpse.domain.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "g5_shop_order")
public class G5ShopOrder {

    @Id
    @Column(name = "od_id")
    private long odId;

    @Column(name = "mb_id")
    private String mbId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mb_id", referencedColumnName = "mb_id", insertable = false, updatable = false)
    private G5Member member;

    @OneToMany(mappedBy = "shopOrder")
    private List<G5ShopCart> carts = new ArrayList<>();

    @Column(name = "od_name", length = 100)
    private String odName;

    @Column(name = "od_email", length = 100)
    private String odEmail;

    @Column(name = "od_tel", length = 20)
    private String odTel;

    @Column(name = "od_hp", length = 20)
    private String odHp;

    @Column(name = "od_zip1", length = 3)
    private String odZip1;

    @Column(name = "od_zip2", length = 3)
    private String odZip2;

    @Column(name = "od_addr1", length = 100)
    private String odAddr1;

    @Column(name = "od_addr2", length = 100)
    private String odAddr2;

    @Column(name = "od_addr3")
    private String odAddr3;

    @Column(name = "od_addr_jibeon")
    private String odAddrJibeon;

    @Column(name = "od_deposit_name", length = 20)
    private String odDepositName;

    @Column(name = "od_b_name", length = 20)
    private String odBName;

    @Column(name = "od_b_tel", length = 20)
    private String odBTel;

    @Column(name = "od_b_hp", length = 20)
    private String odBHp;

    @Column(name = "od_b_zip1", length = 3)
    private String odBZip1;

    @Column(name = "od_b_zip2", length = 3)
    private String odBZip2;

    @Column(name = "od_b_addr1", length = 100)
    private String odBAddr1;

    @Column(name = "od_b_addr2", length = 100)
    private String odBAddr2;

    @Column(name = "od_b_addr3")
    private String odBAddr3;

    @Column(name = "od_b_addr_jibeon")
    private String odBAddrJibeon;

    @Lob
    @Column(name = "od_memo", columnDefinition = "text")
    private String odMemo;

    @Column(name = "od_cart_count")
    private int odCartCount;

    @Column(name = "od_cart_price")
    private int odCartPrice;

    @Column(name = "od_cart_coupon")
    private int odCartCoupon;

    @Column(name = "od_send_cost")
    private int odSendCost;

    @Column(name = "od_send_cost2")
    private int odSendCost2;

    @Column(name = "od_send_coupon")
    private int odSendCoupon;

    @Column(name = "od_receipt_price")
    private int odReceiptPrice;

    @Column(name = "od_cancel_price")
    private int odCancelPrice;

    @Column(name = "od_receipt_point")
    private int odReceiptPoint;

    @Column(name = "od_refund_price")
    private int odRefundPrice;

    @Column(name = "od_bank_account")
    private String odBankAccount;

    @Column(name = "od_receipt_time")
    private Date odReceiptTime;

    @Column(name = "od_coupon")
    private int odCoupon;

    @Column(name = "od_misu")
    private int odMisu;

    @Lob
    @Column(name = "od_shop_memo", columnDefinition = "text")
    private String odShopMemo;

    @Lob
    @Column(name = "od_mod_history", columnDefinition = "text")
    private String odModHistory;

    @Column(name = "od_status")
    private String odStatus;

    @Column(name = "od_hope_date")
    private Date odHopeDate;

    @Column(name = "od_settle_case")
    private String odSettleCase;

    @Column(name = "od_test")
    private int odTest;

    @Column(name = "od_mobile")
    private int odMobile;

    @Column(name = "od_pg")
    private String odPg;

    @Column(name = "od_tno")
    private String odTno;

    @Column(name = "od_app_no", length = 20)
    private String odAppNo;

    @Column(name = "od_escrow")
    private int odEscrow;

    @Column(name = "od_casseqno")
    private String odCasseqno;

    @Column(name = "od_tax_flag")
    private int odTaxFlag;

    @Column(name = "od_tax_mny")
    private int odTaxMny;

    @Column(name = "od_vat_mny")
    private int odVatMny;

    @Column(name = "od_free_mny")
    private int odFreeMny;

    @Column(name = "od_delivery_company")
    private String odDeliveryCompany;

    @Column(name = "od_invoice")
    private String odInvoice;

    @Column(name = "od_invoice_time")
    private Date odInvoiceTime;

    @Column(name = "od_cash")
    private int odCash;

    @Column(name = "od_cash_no")
    private String odCashNo;

    @Lob
    @Column(name = "od_cash_info", columnDefinition = "text")
    private String odCashInfo;

    @Column(name = "od_time")
    private Date odTime;

    @Column(name = "od_pwd")
    private String odPwd;

    @Column(name = "od_ip", length = 25)
    private String odIp;

    @Column(name = "od_1", length = 60)
    private String od1;

    @Column(name = "od_2", length = 60)
    private String od2;

    @Column(name = "od_3", length = 60)
    private String od3;

    @Column(name = "od_4", length = 60)
    private String od4;

    @Column(name = "od_5", length = 60)
    private String od5;

    @Column(name = "od_6", length = 60)
    private String od6;

    @Column(name = "od_7", length = 60)
    private String od7;

    @Column(name = "od_8", length = 60)
    private String od8;

    @Column(name = "od_9", length = 60)
    private String od9;

    @Column(name = "od_10", length = 60)
    private String od10;

    @Column(name = "od_11", length = 60)
    private String od11;

    @Column(name = "od_12", length = 60)
    private String od12;

    @Column(name = "od_13", length = 60)
    private String od13;

    @Column(name = "od_14", length = 60)
    private String od14;

    @Column(name = "od_15", length = 60)
    private String od15;

    @Column(name = "od_16", length = 60)
    private String od16;

    @Column(name = "od_17", length = 60)
    private String od17;

    public long getOdId() { return odId; }
    public void setOdId(long odId) { this.odId = odId; }
    public String getMbId() { return mbId; }
    public void setMbId(String mbId) { this.mbId = mbId; }
    public G5Member getMember() { return member; }
    public void setMember(G5Member member) { this.member = member; }
    public List<G5ShopCart> getCarts() { return carts; }
    public void setCarts(List<G5ShopCart> carts) { this.carts = carts; }
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
    public String getOdShopMemo() { return odShopMemo; }
    public void setOdShopMemo(String odShopMemo) { this.odShopMemo = odShopMemo; }
    public String getOdModHistory() { return odModHistory; }
    public void setOdModHistory(String odModHistory) { this.odModHistory = odModHistory; }
    public String getOdStatus() { return odStatus; }
    public void setOdStatus(String odStatus) { this.odStatus = odStatus; }
    public Date getOdHopeDate() { return odHopeDate; }
    public void setOdHopeDate(Date odHopeDate) { this.odHopeDate = odHopeDate; }
    public String getOdSettleCase() { return odSettleCase; }
    public void setOdSettleCase(String odSettleCase) { this.odSettleCase = odSettleCase; }
    public int getOdTest() { return odTest; }
    public void setOdTest(int odTest) { this.odTest = odTest; }
    public int getOdMobile() { return odMobile; }
    public void setOdMobile(int odMobile) { this.odMobile = odMobile; }
    public String getOdPg() { return odPg; }
    public void setOdPg(String odPg) { this.odPg = odPg; }
    public String getOdTno() { return odTno; }
    public void setOdTno(String odTno) { this.odTno = odTno; }
    public String getOdAppNo() { return odAppNo; }
    public void setOdAppNo(String odAppNo) { this.odAppNo = odAppNo; }
    public int getOdEscrow() { return odEscrow; }
    public void setOdEscrow(int odEscrow) { this.odEscrow = odEscrow; }
    public String getOdCasseqno() { return odCasseqno; }
    public void setOdCasseqno(String odCasseqno) { this.odCasseqno = odCasseqno; }
    public int getOdTaxFlag() { return odTaxFlag; }
    public void setOdTaxFlag(int odTaxFlag) { this.odTaxFlag = odTaxFlag; }
    public int getOdTaxMny() { return odTaxMny; }
    public void setOdTaxMny(int odTaxMny) { this.odTaxMny = odTaxMny; }
    public int getOdVatMny() { return odVatMny; }
    public void setOdVatMny(int odVatMny) { this.odVatMny = odVatMny; }
    public int getOdFreeMny() { return odFreeMny; }
    public void setOdFreeMny(int odFreeMny) { this.odFreeMny = odFreeMny; }
    public String getOdDeliveryCompany() { return odDeliveryCompany; }
    public void setOdDeliveryCompany(String odDeliveryCompany) { this.odDeliveryCompany = odDeliveryCompany; }
    public String getOdInvoice() { return odInvoice; }
    public void setOdInvoice(String odInvoice) { this.odInvoice = odInvoice; }
    public Date getOdInvoiceTime() { return odInvoiceTime; }
    public void setOdInvoiceTime(Date odInvoiceTime) { this.odInvoiceTime = odInvoiceTime; }
    public int getOdCash() { return odCash; }
    public void setOdCash(int odCash) { this.odCash = odCash; }
    public String getOdCashNo() { return odCashNo; }
    public void setOdCashNo(String odCashNo) { this.odCashNo = odCashNo; }
    public String getOdCashInfo() { return odCashInfo; }
    public void setOdCashInfo(String odCashInfo) { this.odCashInfo = odCashInfo; }
    public Date getOdTime() { return odTime; }
    public void setOdTime(Date odTime) { this.odTime = odTime; }
    public String getOdPwd() { return odPwd; }
    public void setOdPwd(String odPwd) { this.odPwd = odPwd; }
    public String getOdIp() { return odIp; }
    public void setOdIp(String odIp) { this.odIp = odIp; }
    public String getOd1() { return od1; }
    public void setOd1(String od1) { this.od1 = od1; }
    public String getOd2() { return od2; }
    public void setOd2(String od2) { this.od2 = od2; }
    public String getOd3() { return od3; }
    public void setOd3(String od3) { this.od3 = od3; }
    public String getOd4() { return od4; }
    public void setOd4(String od4) { this.od4 = od4; }
    public String getOd5() { return od5; }
    public void setOd5(String od5) { this.od5 = od5; }
    public String getOd6() { return od6; }
    public void setOd6(String od6) { this.od6 = od6; }
    public String getOd7() { return od7; }
    public void setOd7(String od7) { this.od7 = od7; }
    public String getOd8() { return od8; }
    public void setOd8(String od8) { this.od8 = od8; }
    public String getOd9() { return od9; }
    public void setOd9(String od9) { this.od9 = od9; }
    public String getOd10() { return od10; }
    public void setOd10(String od10) { this.od10 = od10; }
    public String getOd11() { return od11; }
    public void setOd11(String od11) { this.od11 = od11; }
    public String getOd12() { return od12; }
    public void setOd12(String od12) { this.od12 = od12; }
    public String getOd13() { return od13; }
    public void setOd13(String od13) { this.od13 = od13; }
    public String getOd14() { return od14; }
    public void setOd14(String od14) { this.od14 = od14; }
    public String getOd15() { return od15; }
    public void setOd15(String od15) { this.od15 = od15; }
    public String getOd16() { return od16; }
    public void setOd16(String od16) { this.od16 = od16; }
    public String getOd17() { return od17; }
    public void setOd17(String od17) { this.od17 = od17; }
}
