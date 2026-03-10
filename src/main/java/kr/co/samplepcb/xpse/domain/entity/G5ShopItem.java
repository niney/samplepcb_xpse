package kr.co.samplepcb.xpse.domain.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "g5_shop_item")
public class G5ShopItem {

    @Id
    @Column(name = "it_id", length = 20)
    private String itId;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "it_id", referencedColumnName = "it_id", insertable = false, updatable = false)
    private List<SpPartnerOrder> partnerOrders;

    @Column(name = "ca_id", length = 10)
    private String caId;

    @Column(name = "ca_id2")
    private String caId2;

    @Column(name = "ca_id3")
    private String caId3;

    @Column(name = "it_skin")
    private String itSkin;

    @Column(name = "it_mobile_skin")
    private String itMobileSkin;

    @Column(name = "it_name")
    private String itName;

    @Column(name = "it_maker")
    private String itMaker;

    @Column(name = "it_origin")
    private String itOrigin;

    @Column(name = "it_brand")
    private String itBrand;

    @Column(name = "it_model")
    private String itModel;

    @Column(name = "it_option_subject")
    private String itOptionSubject;

    @Column(name = "it_supply_subject")
    private String itSupplySubject;

    @Column(name = "it_type1")
    private int itType1;

    @Column(name = "it_type2")
    private int itType2;

    @Column(name = "it_type3")
    private int itType3;

    @Column(name = "it_type4")
    private int itType4;

    @Column(name = "it_type5")
    private int itType5;

    @Lob
    @Column(name = "it_basic", columnDefinition = "longtext")
    private String itBasic;

    @Lob
    @Column(name = "it_explan", columnDefinition = "longtext")
    private String itExplan;

    @Lob
    @Column(name = "it_explan2", columnDefinition = "longtext")
    private String itExplan2;

    @Lob
    @Column(name = "it_mobile_explan", columnDefinition = "mediumtext")
    private String itMobileExplan;

    @Column(name = "it_cust_price")
    private int itCustPrice;

    @Column(name = "it_price")
    private int itPrice;

    @Column(name = "it_point")
    private int itPoint;

    @Column(name = "it_point_type")
    private int itPointType;

    @Column(name = "it_supply_point")
    private int itSupplyPoint;

    @Column(name = "it_notax")
    private int itNotax;

    @Column(name = "it_sell_email")
    private String itSellEmail;

    @Column(name = "it_use")
    private int itUse;

    @Column(name = "it_nocoupon")
    private int itNocoupon;

    @Column(name = "it_soldout")
    private int itSoldout;

    @Column(name = "it_stock_qty")
    private int itStockQty;

    @Column(name = "it_stock_sms")
    private int itStockSms;

    @Column(name = "it_noti_qty")
    private int itNotiQty;

    @Column(name = "it_sc_type")
    private int itScType;

    @Column(name = "it_sc_method")
    private int itScMethod;

    @Column(name = "it_sc_price")
    private int itScPrice;

    @Column(name = "it_sc_minimum")
    private int itScMinimum;

    @Column(name = "it_sc_qty")
    private int itScQty;

    @Column(name = "it_buy_min_qty")
    private int itBuyMinQty;

    @Column(name = "it_buy_max_qty")
    private int itBuyMaxQty;

    @Lob
    @Column(name = "it_head_html", columnDefinition = "text")
    private String itHeadHtml;

    @Lob
    @Column(name = "it_tail_html", columnDefinition = "text")
    private String itTailHtml;

    @Lob
    @Column(name = "it_mobile_head_html", columnDefinition = "text")
    private String itMobileHeadHtml;

    @Lob
    @Column(name = "it_mobile_tail_html", columnDefinition = "text")
    private String itMobileTailHtml;

    @Column(name = "it_hit")
    private int itHit;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "it_time")
    private Date itTime;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "it_update_time")
    private Date itUpdateTime;

    @Column(name = "it_ip", length = 25)
    private String itIp;

    @Column(name = "it_order")
    private int itOrder;

    @Column(name = "it_tel_inq")
    private int itTelInq;

    @Column(name = "it_info_gubun", length = 50)
    private String itInfoGubun;

    @Lob
    @Column(name = "it_info_value", columnDefinition = "text")
    private String itInfoValue;

    @Column(name = "it_sum_qty")
    private int itSumQty;

    @Column(name = "it_use_cnt")
    private int itUseCnt;

    @Column(name = "it_use_avg", precision = 2, scale = 1)
    private BigDecimal itUseAvg;

    @Lob
    @Column(name = "it_shop_memo", columnDefinition = "text")
    private String itShopMemo;

    @Column(name = "ec_mall_pid")
    private String ecMallPid;

    @Column(name = "it_company_name", length = 30)
    private String itCompanyName;

    @Column(name = "it_member_name", length = 30)
    private String itMemberName;

    @Column(name = "it_member_tel", length = 30)
    private String itMemberTel;

    @Column(name = "it_member_mail", length = 30)
    private String itMemberMail;

    @Column(name = "it_member_memo", length = 4000)
    private String itMemberMemo;

    @Column(name = "it_eta", length = 30)
    private String itEta;

    @Column(name = "it_img1")
    private String itImg1;

    @Column(name = "it_img2")
    private String itImg2;

    @Column(name = "it_img3")
    private String itImg3;

    @Column(name = "it_img4")
    private String itImg4;

    @Column(name = "it_img5")
    private String itImg5;

    @Column(name = "it_img6")
    private String itImg6;

    @Column(name = "it_img7")
    private String itImg7;

    @Column(name = "it_img8")
    private String itImg8;

    @Column(name = "it_img9")
    private String itImg9;

    @Column(name = "it_img10")
    private String itImg10;

    @Column(name = "it_file1", length = 150)
    private String itFile1;

    @Column(name = "it_file2", length = 150)
    private String itFile2;

    @Column(name = "it_file3", length = 150)
    private String itFile3;

    @Column(name = "it_file4", length = 150)
    private String itFile4;

    @Column(name = "it_file5", length = 150)
    private String itFile5;

    @Column(name = "it_file6", length = 150)
    private String itFile6;

    @Column(name = "it_file7", length = 150)
    private String itFile7;

    @Column(name = "it_file8", length = 150)
    private String itFile8;

    @Column(name = "it_1_subj", length = 30)
    private String it1Subj;

    @Column(name = "it_2_subj", length = 30)
    private String it2Subj;

    @Column(name = "it_3_subj", length = 30)
    private String it3Subj;

    @Column(name = "it_4_subj", length = 30)
    private String it4Subj;

    @Column(name = "it_5_subj", length = 30)
    private String it5Subj;

    @Column(name = "it_6_subj", length = 30)
    private String it6Subj;

    @Column(name = "it_7_subj", length = 30)
    private String it7Subj;

    @Column(name = "it_8_subj", length = 30)
    private String it8Subj;

    @Column(name = "it_9_subj", length = 30)
    private String it9Subj;

    @Column(name = "it_10_subj", length = 30)
    private String it10Subj;

    @Column(name = "it_1")
    private String it1;

    @Column(name = "it_2")
    private String it2;

    @Column(name = "it_3")
    private String it3;

    @Column(name = "it_4")
    private String it4;

    @Column(name = "it_5")
    private String it5;

    @Column(name = "it_6")
    private String it6;

    @Column(name = "it_7")
    private String it7;

    @Column(name = "it_8")
    private String it8;

    @Column(name = "it_9")
    private String it9;

    @Column(name = "it_10")
    private String it10;

    @Column(name = "it_11_subj", length = 30)
    private String it11Subj;

    @Column(name = "it_11")
    private String it11;

    @Column(name = "it_12_subj", length = 30)
    private String it12Subj;

    @Column(name = "it_12")
    private String it12;

    @Column(name = "it_13_subj", length = 30)
    private String it13Subj;

    @Column(name = "it_13")
    private String it13;

    @Column(name = "it_14_subj", length = 30)
    private String it14Subj;

    @Column(name = "it_14")
    private String it14;

    @Column(name = "it_15_subj", length = 30)
    private String it15Subj;

    @Column(name = "it_15")
    private String it15;

    @Column(name = "it_16_subj", length = 30)
    private String it16Subj;

    @Column(name = "it_16")
    private String it16;

    @Column(name = "it_17_subj", length = 30)
    private String it17Subj;

    @Column(name = "it_17")
    private String it17;

    @Column(name = "it_18_subj", length = 30)
    private String it18Subj;

    @Column(name = "it_18")
    private String it18;

    @Column(name = "it_19_subj", length = 30)
    private String it19Subj;

    @Column(name = "it_19", length = 30)
    private String it19;

    @Column(name = "it_20_subj", length = 20)
    private String it20Subj;

    @Column(name = "it_20", length = 30)
    private String it20;

    @Column(name = "it_21_subj", length = 30)
    private String it21Subj;

    @Column(name = "it_21", length = 30)
    private String it21;

    @Column(name = "it_22_subj", length = 30)
    private String it22Subj;

    @Column(name = "it_22", length = 30)
    private String it22;

    @Column(name = "it_23_subj", length = 30)
    private String it23Subj;

    @Column(name = "it_23", length = 30)
    private String it23;

    @Column(name = "it_24_subj", length = 30)
    private String it24Subj;

    @Column(name = "it_24", length = 30)
    private String it24;

    @Column(name = "it_25_subj", length = 30)
    private String it25Subj;

    @Column(name = "it_25", length = 30)
    private String it25;

    @Column(name = "it_26_subj", length = 30)
    private String it26Subj;

    @Column(name = "it_26", length = 30)
    private String it26;

    @Column(name = "it_27_subj", length = 30)
    private String it27Subj;

    @Column(name = "it_27", length = 30)
    private String it27;

    @Column(name = "it_28_subj", length = 30)
    private String it28Subj;

    @Column(name = "it_28", length = 30)
    private String it28;

    @Column(name = "it_29_subj", length = 30)
    private String it29Subj;

    @Column(name = "it_29", length = 30)
    private String it29;

    @Column(name = "it_30_subj", length = 30)
    private String it30Subj;

    @Column(name = "it_30_subj2", length = 20)
    private String it30Subj2;

    @Column(name = "it_30_subj3", length = 20)
    private String it30Subj3;

    @Column(name = "it_30", length = 30)
    private String it30;

    @Column(name = "it_31_subj", length = 30)
    private String it31Subj;

    @Column(name = "it_31_subj2", length = 20)
    private String it31Subj2;

    @Column(name = "it_31_subj3", length = 20)
    private String it31Subj3;

    @Column(name = "it_31", length = 30)
    private String it31;

    @Column(name = "it_32_subj", length = 30)
    private String it32Subj;

    @Column(name = "it_32_subj2", length = 20)
    private String it32Subj2;

    @Column(name = "it_32_subj3", length = 20)
    private String it32Subj3;

    @Column(name = "it_32", length = 30)
    private String it32;

    @Column(name = "it_33_subj", length = 30)
    private String it33Subj;

    @Column(name = "it_33_subj2", length = 20)
    private String it33Subj2;

    @Column(name = "it_33_subj3", length = 20)
    private String it33Subj3;

    @Column(name = "it_33", length = 30)
    private String it33;

    @Column(name = "it_34_subj", length = 30)
    private String it34Subj;

    @Column(name = "it_34_subj2", length = 20)
    private String it34Subj2;

    @Column(name = "it_34_subj3", length = 20)
    private String it34Subj3;

    @Column(name = "it_34", length = 30)
    private String it34;

    @Column(name = "it_35_subj", length = 30)
    private String it35Subj;

    @Column(name = "it_35_subj2", length = 20)
    private String it35Subj2;

    @Column(name = "it_35_subj3", length = 20)
    private String it35Subj3;

    @Column(name = "it_35", length = 30)
    private String it35;

    @Column(name = "it_36_subj", length = 30)
    private String it36Subj;

    @Column(name = "it_36_subj2", length = 20)
    private String it36Subj2;

    @Column(name = "it_36_subj3", length = 20)
    private String it36Subj3;

    @Column(name = "it_36", length = 30)
    private String it36;

    @Column(name = "it_37_subj", length = 30)
    private String it37Subj;

    @Column(name = "it_37_subj2", length = 20)
    private String it37Subj2;

    @Column(name = "it_37_subj3", length = 20)
    private String it37Subj3;

    @Column(name = "it_37", length = 30)
    private String it37;

    @Column(name = "it_38_subj", length = 30)
    private String it38Subj;

    @Column(name = "it_38_subj2", length = 20)
    private String it38Subj2;

    @Column(name = "it_38_subj3", length = 20)
    private String it38Subj3;

    @Column(name = "it_38", length = 30)
    private String it38;

    @Column(name = "it_39_subj", length = 30)
    private String it39Subj;

    @Column(name = "it_39_subj2", length = 20)
    private String it39Subj2;

    @Column(name = "it_39_subj3", length = 20)
    private String it39Subj3;

    @Column(name = "it_39", length = 30)
    private String it39;

    @Column(name = "it_40_subj", length = 30)
    private String it40Subj;

    @Column(name = "it_40", length = 30)
    private String it40;

    @Column(name = "it_41_subj", length = 30)
    private String it41Subj;

    @Column(name = "it_41", length = 30)
    private String it41;

    @Column(name = "it_42_subj", length = 30)
    private String it42Subj;

    @Column(name = "it_42", length = 30)
    private String it42;

    @Column(name = "it_43_subj", length = 30)
    private String it43Subj;

    @Column(name = "it_43", length = 30)
    private String it43;

    @Column(name = "it_44_subj", length = 30)
    private String it44Subj;

    @Column(name = "it_44", length = 30)
    private String it44;

    @Column(name = "it_45_subj", length = 30)
    private String it45Subj;

    @Column(name = "it_45", length = 30)
    private String it45;

    @Column(name = "it_46_subj", length = 30)
    private String it46Subj;

    @Column(name = "it_46", length = 30)
    private String it46;

    @Column(name = "it_47_subj", length = 30)
    private String it47Subj;

    @Column(name = "it_47", length = 30)
    private String it47;

    @Column(name = "it_48_subj", length = 30)
    private String it48Subj;

    @Column(name = "it_48", length = 30)
    private String it48;

    @Column(name = "it_49_subj", length = 30)
    private String it49Subj;

    @Column(name = "it_49", length = 30)
    private String it49;

    @Column(name = "it_50_subj", length = 30)
    private String it50Subj;

    @Column(name = "it_50", length = 30)
    private String it50;

    public String getItId() { return itId; }
    public void setItId(String itId) { this.itId = itId; }
    public String getCaId() { return caId; }
    public void setCaId(String caId) { this.caId = caId; }
    public String getCaId2() { return caId2; }
    public void setCaId2(String caId2) { this.caId2 = caId2; }
    public String getCaId3() { return caId3; }
    public void setCaId3(String caId3) { this.caId3 = caId3; }
    public String getItSkin() { return itSkin; }
    public void setItSkin(String itSkin) { this.itSkin = itSkin; }
    public String getItMobileSkin() { return itMobileSkin; }
    public void setItMobileSkin(String itMobileSkin) { this.itMobileSkin = itMobileSkin; }
    public String getItName() { return itName; }
    public void setItName(String itName) { this.itName = itName; }
    public String getItMaker() { return itMaker; }
    public void setItMaker(String itMaker) { this.itMaker = itMaker; }
    public String getItOrigin() { return itOrigin; }
    public void setItOrigin(String itOrigin) { this.itOrigin = itOrigin; }
    public String getItBrand() { return itBrand; }
    public void setItBrand(String itBrand) { this.itBrand = itBrand; }
    public String getItModel() { return itModel; }
    public void setItModel(String itModel) { this.itModel = itModel; }
    public String getItOptionSubject() { return itOptionSubject; }
    public void setItOptionSubject(String itOptionSubject) { this.itOptionSubject = itOptionSubject; }
    public String getItSupplySubject() { return itSupplySubject; }
    public void setItSupplySubject(String itSupplySubject) { this.itSupplySubject = itSupplySubject; }
    public int getItType1() { return itType1; }
    public void setItType1(int itType1) { this.itType1 = itType1; }
    public int getItType2() { return itType2; }
    public void setItType2(int itType2) { this.itType2 = itType2; }
    public int getItType3() { return itType3; }
    public void setItType3(int itType3) { this.itType3 = itType3; }
    public int getItType4() { return itType4; }
    public void setItType4(int itType4) { this.itType4 = itType4; }
    public int getItType5() { return itType5; }
    public void setItType5(int itType5) { this.itType5 = itType5; }
    public String getItBasic() { return itBasic; }
    public void setItBasic(String itBasic) { this.itBasic = itBasic; }
    public String getItExplan() { return itExplan; }
    public void setItExplan(String itExplan) { this.itExplan = itExplan; }
    public String getItExplan2() { return itExplan2; }
    public void setItExplan2(String itExplan2) { this.itExplan2 = itExplan2; }
    public String getItMobileExplan() { return itMobileExplan; }
    public void setItMobileExplan(String itMobileExplan) { this.itMobileExplan = itMobileExplan; }
    public int getItCustPrice() { return itCustPrice; }
    public void setItCustPrice(int itCustPrice) { this.itCustPrice = itCustPrice; }
    public int getItPrice() { return itPrice; }
    public void setItPrice(int itPrice) { this.itPrice = itPrice; }
    public int getItPoint() { return itPoint; }
    public void setItPoint(int itPoint) { this.itPoint = itPoint; }
    public int getItPointType() { return itPointType; }
    public void setItPointType(int itPointType) { this.itPointType = itPointType; }
    public int getItSupplyPoint() { return itSupplyPoint; }
    public void setItSupplyPoint(int itSupplyPoint) { this.itSupplyPoint = itSupplyPoint; }
    public int getItNotax() { return itNotax; }
    public void setItNotax(int itNotax) { this.itNotax = itNotax; }
    public String getItSellEmail() { return itSellEmail; }
    public void setItSellEmail(String itSellEmail) { this.itSellEmail = itSellEmail; }
    public int getItUse() { return itUse; }
    public void setItUse(int itUse) { this.itUse = itUse; }
    public int getItNocoupon() { return itNocoupon; }
    public void setItNocoupon(int itNocoupon) { this.itNocoupon = itNocoupon; }
    public int getItSoldout() { return itSoldout; }
    public void setItSoldout(int itSoldout) { this.itSoldout = itSoldout; }
    public int getItStockQty() { return itStockQty; }
    public void setItStockQty(int itStockQty) { this.itStockQty = itStockQty; }
    public int getItStockSms() { return itStockSms; }
    public void setItStockSms(int itStockSms) { this.itStockSms = itStockSms; }
    public int getItNotiQty() { return itNotiQty; }
    public void setItNotiQty(int itNotiQty) { this.itNotiQty = itNotiQty; }
    public int getItScType() { return itScType; }
    public void setItScType(int itScType) { this.itScType = itScType; }
    public int getItScMethod() { return itScMethod; }
    public void setItScMethod(int itScMethod) { this.itScMethod = itScMethod; }
    public int getItScPrice() { return itScPrice; }
    public void setItScPrice(int itScPrice) { this.itScPrice = itScPrice; }
    public int getItScMinimum() { return itScMinimum; }
    public void setItScMinimum(int itScMinimum) { this.itScMinimum = itScMinimum; }
    public int getItScQty() { return itScQty; }
    public void setItScQty(int itScQty) { this.itScQty = itScQty; }
    public int getItBuyMinQty() { return itBuyMinQty; }
    public void setItBuyMinQty(int itBuyMinQty) { this.itBuyMinQty = itBuyMinQty; }
    public int getItBuyMaxQty() { return itBuyMaxQty; }
    public void setItBuyMaxQty(int itBuyMaxQty) { this.itBuyMaxQty = itBuyMaxQty; }
    public String getItHeadHtml() { return itHeadHtml; }
    public void setItHeadHtml(String itHeadHtml) { this.itHeadHtml = itHeadHtml; }
    public String getItTailHtml() { return itTailHtml; }
    public void setItTailHtml(String itTailHtml) { this.itTailHtml = itTailHtml; }
    public String getItMobileHeadHtml() { return itMobileHeadHtml; }
    public void setItMobileHeadHtml(String itMobileHeadHtml) { this.itMobileHeadHtml = itMobileHeadHtml; }
    public String getItMobileTailHtml() { return itMobileTailHtml; }
    public void setItMobileTailHtml(String itMobileTailHtml) { this.itMobileTailHtml = itMobileTailHtml; }
    public int getItHit() { return itHit; }
    public void setItHit(int itHit) { this.itHit = itHit; }
    public Date getItTime() { return itTime; }
    public void setItTime(Date itTime) { this.itTime = itTime; }
    public Date getItUpdateTime() { return itUpdateTime; }
    public void setItUpdateTime(Date itUpdateTime) { this.itUpdateTime = itUpdateTime; }
    public String getItIp() { return itIp; }
    public void setItIp(String itIp) { this.itIp = itIp; }
    public int getItOrder() { return itOrder; }
    public void setItOrder(int itOrder) { this.itOrder = itOrder; }
    public int getItTelInq() { return itTelInq; }
    public void setItTelInq(int itTelInq) { this.itTelInq = itTelInq; }
    public String getItInfoGubun() { return itInfoGubun; }
    public void setItInfoGubun(String itInfoGubun) { this.itInfoGubun = itInfoGubun; }
    public String getItInfoValue() { return itInfoValue; }
    public void setItInfoValue(String itInfoValue) { this.itInfoValue = itInfoValue; }
    public int getItSumQty() { return itSumQty; }
    public void setItSumQty(int itSumQty) { this.itSumQty = itSumQty; }
    public int getItUseCnt() { return itUseCnt; }
    public void setItUseCnt(int itUseCnt) { this.itUseCnt = itUseCnt; }
    public BigDecimal getItUseAvg() { return itUseAvg; }
    public void setItUseAvg(BigDecimal itUseAvg) { this.itUseAvg = itUseAvg; }
    public String getItShopMemo() { return itShopMemo; }
    public void setItShopMemo(String itShopMemo) { this.itShopMemo = itShopMemo; }
    public String getEcMallPid() { return ecMallPid; }
    public void setEcMallPid(String ecMallPid) { this.ecMallPid = ecMallPid; }
    public String getItCompanyName() { return itCompanyName; }
    public void setItCompanyName(String itCompanyName) { this.itCompanyName = itCompanyName; }
    public String getItMemberName() { return itMemberName; }
    public void setItMemberName(String itMemberName) { this.itMemberName = itMemberName; }
    public String getItMemberTel() { return itMemberTel; }
    public void setItMemberTel(String itMemberTel) { this.itMemberTel = itMemberTel; }
    public String getItMemberMail() { return itMemberMail; }
    public void setItMemberMail(String itMemberMail) { this.itMemberMail = itMemberMail; }
    public String getItMemberMemo() { return itMemberMemo; }
    public void setItMemberMemo(String itMemberMemo) { this.itMemberMemo = itMemberMemo; }
    public String getItEta() { return itEta; }
    public void setItEta(String itEta) { this.itEta = itEta; }
    public String getItImg1() { return itImg1; }
    public void setItImg1(String itImg1) { this.itImg1 = itImg1; }
    public String getItImg2() { return itImg2; }
    public void setItImg2(String itImg2) { this.itImg2 = itImg2; }
    public String getItImg3() { return itImg3; }
    public void setItImg3(String itImg3) { this.itImg3 = itImg3; }
    public String getItImg4() { return itImg4; }
    public void setItImg4(String itImg4) { this.itImg4 = itImg4; }
    public String getItImg5() { return itImg5; }
    public void setItImg5(String itImg5) { this.itImg5 = itImg5; }
    public String getItImg6() { return itImg6; }
    public void setItImg6(String itImg6) { this.itImg6 = itImg6; }
    public String getItImg7() { return itImg7; }
    public void setItImg7(String itImg7) { this.itImg7 = itImg7; }
    public String getItImg8() { return itImg8; }
    public void setItImg8(String itImg8) { this.itImg8 = itImg8; }
    public String getItImg9() { return itImg9; }
    public void setItImg9(String itImg9) { this.itImg9 = itImg9; }
    public String getItImg10() { return itImg10; }
    public void setItImg10(String itImg10) { this.itImg10 = itImg10; }
    public String getItFile1() { return itFile1; }
    public void setItFile1(String itFile1) { this.itFile1 = itFile1; }
    public String getItFile2() { return itFile2; }
    public void setItFile2(String itFile2) { this.itFile2 = itFile2; }
    public String getItFile3() { return itFile3; }
    public void setItFile3(String itFile3) { this.itFile3 = itFile3; }
    public String getItFile4() { return itFile4; }
    public void setItFile4(String itFile4) { this.itFile4 = itFile4; }
    public String getItFile5() { return itFile5; }
    public void setItFile5(String itFile5) { this.itFile5 = itFile5; }
    public String getItFile6() { return itFile6; }
    public void setItFile6(String itFile6) { this.itFile6 = itFile6; }
    public String getItFile7() { return itFile7; }
    public void setItFile7(String itFile7) { this.itFile7 = itFile7; }
    public String getItFile8() { return itFile8; }
    public void setItFile8(String itFile8) { this.itFile8 = itFile8; }
    public String getIt1Subj() { return it1Subj; }
    public void setIt1Subj(String it1Subj) { this.it1Subj = it1Subj; }
    public String getIt2Subj() { return it2Subj; }
    public void setIt2Subj(String it2Subj) { this.it2Subj = it2Subj; }
    public String getIt3Subj() { return it3Subj; }
    public void setIt3Subj(String it3Subj) { this.it3Subj = it3Subj; }
    public String getIt4Subj() { return it4Subj; }
    public void setIt4Subj(String it4Subj) { this.it4Subj = it4Subj; }
    public String getIt5Subj() { return it5Subj; }
    public void setIt5Subj(String it5Subj) { this.it5Subj = it5Subj; }
    public String getIt6Subj() { return it6Subj; }
    public void setIt6Subj(String it6Subj) { this.it6Subj = it6Subj; }
    public String getIt7Subj() { return it7Subj; }
    public void setIt7Subj(String it7Subj) { this.it7Subj = it7Subj; }
    public String getIt8Subj() { return it8Subj; }
    public void setIt8Subj(String it8Subj) { this.it8Subj = it8Subj; }
    public String getIt9Subj() { return it9Subj; }
    public void setIt9Subj(String it9Subj) { this.it9Subj = it9Subj; }
    public String getIt10Subj() { return it10Subj; }
    public void setIt10Subj(String it10Subj) { this.it10Subj = it10Subj; }
    public String getIt1() { return it1; }
    public void setIt1(String it1) { this.it1 = it1; }
    public String getIt2() { return it2; }
    public void setIt2(String it2) { this.it2 = it2; }
    public String getIt3() { return it3; }
    public void setIt3(String it3) { this.it3 = it3; }
    public String getIt4() { return it4; }
    public void setIt4(String it4) { this.it4 = it4; }
    public String getIt5() { return it5; }
    public void setIt5(String it5) { this.it5 = it5; }
    public String getIt6() { return it6; }
    public void setIt6(String it6) { this.it6 = it6; }
    public String getIt7() { return it7; }
    public void setIt7(String it7) { this.it7 = it7; }
    public String getIt8() { return it8; }
    public void setIt8(String it8) { this.it8 = it8; }
    public String getIt9() { return it9; }
    public void setIt9(String it9) { this.it9 = it9; }
    public String getIt10() { return it10; }
    public void setIt10(String it10) { this.it10 = it10; }
    public String getIt11Subj() { return it11Subj; }
    public void setIt11Subj(String it11Subj) { this.it11Subj = it11Subj; }
    public String getIt11() { return it11; }
    public void setIt11(String it11) { this.it11 = it11; }
    public String getIt12Subj() { return it12Subj; }
    public void setIt12Subj(String it12Subj) { this.it12Subj = it12Subj; }
    public String getIt12() { return it12; }
    public void setIt12(String it12) { this.it12 = it12; }
    public String getIt13Subj() { return it13Subj; }
    public void setIt13Subj(String it13Subj) { this.it13Subj = it13Subj; }
    public String getIt13() { return it13; }
    public void setIt13(String it13) { this.it13 = it13; }
    public String getIt14Subj() { return it14Subj; }
    public void setIt14Subj(String it14Subj) { this.it14Subj = it14Subj; }
    public String getIt14() { return it14; }
    public void setIt14(String it14) { this.it14 = it14; }
    public String getIt15Subj() { return it15Subj; }
    public void setIt15Subj(String it15Subj) { this.it15Subj = it15Subj; }
    public String getIt15() { return it15; }
    public void setIt15(String it15) { this.it15 = it15; }
    public String getIt16Subj() { return it16Subj; }
    public void setIt16Subj(String it16Subj) { this.it16Subj = it16Subj; }
    public String getIt16() { return it16; }
    public void setIt16(String it16) { this.it16 = it16; }
    public String getIt17Subj() { return it17Subj; }
    public void setIt17Subj(String it17Subj) { this.it17Subj = it17Subj; }
    public String getIt17() { return it17; }
    public void setIt17(String it17) { this.it17 = it17; }
    public String getIt18Subj() { return it18Subj; }
    public void setIt18Subj(String it18Subj) { this.it18Subj = it18Subj; }
    public String getIt18() { return it18; }
    public void setIt18(String it18) { this.it18 = it18; }
    public String getIt19Subj() { return it19Subj; }
    public void setIt19Subj(String it19Subj) { this.it19Subj = it19Subj; }
    public String getIt19() { return it19; }
    public void setIt19(String it19) { this.it19 = it19; }
    public String getIt20Subj() { return it20Subj; }
    public void setIt20Subj(String it20Subj) { this.it20Subj = it20Subj; }
    public String getIt20() { return it20; }
    public void setIt20(String it20) { this.it20 = it20; }
    public String getIt21Subj() { return it21Subj; }
    public void setIt21Subj(String it21Subj) { this.it21Subj = it21Subj; }
    public String getIt21() { return it21; }
    public void setIt21(String it21) { this.it21 = it21; }
    public String getIt22Subj() { return it22Subj; }
    public void setIt22Subj(String it22Subj) { this.it22Subj = it22Subj; }
    public String getIt22() { return it22; }
    public void setIt22(String it22) { this.it22 = it22; }
    public String getIt23Subj() { return it23Subj; }
    public void setIt23Subj(String it23Subj) { this.it23Subj = it23Subj; }
    public String getIt23() { return it23; }
    public void setIt23(String it23) { this.it23 = it23; }
    public String getIt24Subj() { return it24Subj; }
    public void setIt24Subj(String it24Subj) { this.it24Subj = it24Subj; }
    public String getIt24() { return it24; }
    public void setIt24(String it24) { this.it24 = it24; }
    public String getIt25Subj() { return it25Subj; }
    public void setIt25Subj(String it25Subj) { this.it25Subj = it25Subj; }
    public String getIt25() { return it25; }
    public void setIt25(String it25) { this.it25 = it25; }
    public String getIt26Subj() { return it26Subj; }
    public void setIt26Subj(String it26Subj) { this.it26Subj = it26Subj; }
    public String getIt26() { return it26; }
    public void setIt26(String it26) { this.it26 = it26; }
    public String getIt27Subj() { return it27Subj; }
    public void setIt27Subj(String it27Subj) { this.it27Subj = it27Subj; }
    public String getIt27() { return it27; }
    public void setIt27(String it27) { this.it27 = it27; }
    public String getIt28Subj() { return it28Subj; }
    public void setIt28Subj(String it28Subj) { this.it28Subj = it28Subj; }
    public String getIt28() { return it28; }
    public void setIt28(String it28) { this.it28 = it28; }
    public String getIt29Subj() { return it29Subj; }
    public void setIt29Subj(String it29Subj) { this.it29Subj = it29Subj; }
    public String getIt29() { return it29; }
    public void setIt29(String it29) { this.it29 = it29; }
    public String getIt30Subj() { return it30Subj; }
    public void setIt30Subj(String it30Subj) { this.it30Subj = it30Subj; }
    public String getIt30Subj2() { return it30Subj2; }
    public void setIt30Subj2(String it30Subj2) { this.it30Subj2 = it30Subj2; }
    public String getIt30Subj3() { return it30Subj3; }
    public void setIt30Subj3(String it30Subj3) { this.it30Subj3 = it30Subj3; }
    public String getIt30() { return it30; }
    public void setIt30(String it30) { this.it30 = it30; }
    public String getIt31Subj() { return it31Subj; }
    public void setIt31Subj(String it31Subj) { this.it31Subj = it31Subj; }
    public String getIt31Subj2() { return it31Subj2; }
    public void setIt31Subj2(String it31Subj2) { this.it31Subj2 = it31Subj2; }
    public String getIt31Subj3() { return it31Subj3; }
    public void setIt31Subj3(String it31Subj3) { this.it31Subj3 = it31Subj3; }
    public String getIt31() { return it31; }
    public void setIt31(String it31) { this.it31 = it31; }
    public String getIt32Subj() { return it32Subj; }
    public void setIt32Subj(String it32Subj) { this.it32Subj = it32Subj; }
    public String getIt32Subj2() { return it32Subj2; }
    public void setIt32Subj2(String it32Subj2) { this.it32Subj2 = it32Subj2; }
    public String getIt32Subj3() { return it32Subj3; }
    public void setIt32Subj3(String it32Subj3) { this.it32Subj3 = it32Subj3; }
    public String getIt32() { return it32; }
    public void setIt32(String it32) { this.it32 = it32; }
    public String getIt33Subj() { return it33Subj; }
    public void setIt33Subj(String it33Subj) { this.it33Subj = it33Subj; }
    public String getIt33Subj2() { return it33Subj2; }
    public void setIt33Subj2(String it33Subj2) { this.it33Subj2 = it33Subj2; }
    public String getIt33Subj3() { return it33Subj3; }
    public void setIt33Subj3(String it33Subj3) { this.it33Subj3 = it33Subj3; }
    public String getIt33() { return it33; }
    public void setIt33(String it33) { this.it33 = it33; }
    public String getIt34Subj() { return it34Subj; }
    public void setIt34Subj(String it34Subj) { this.it34Subj = it34Subj; }
    public String getIt34Subj2() { return it34Subj2; }
    public void setIt34Subj2(String it34Subj2) { this.it34Subj2 = it34Subj2; }
    public String getIt34Subj3() { return it34Subj3; }
    public void setIt34Subj3(String it34Subj3) { this.it34Subj3 = it34Subj3; }
    public String getIt34() { return it34; }
    public void setIt34(String it34) { this.it34 = it34; }
    public String getIt35Subj() { return it35Subj; }
    public void setIt35Subj(String it35Subj) { this.it35Subj = it35Subj; }
    public String getIt35Subj2() { return it35Subj2; }
    public void setIt35Subj2(String it35Subj2) { this.it35Subj2 = it35Subj2; }
    public String getIt35Subj3() { return it35Subj3; }
    public void setIt35Subj3(String it35Subj3) { this.it35Subj3 = it35Subj3; }
    public String getIt35() { return it35; }
    public void setIt35(String it35) { this.it35 = it35; }
    public String getIt36Subj() { return it36Subj; }
    public void setIt36Subj(String it36Subj) { this.it36Subj = it36Subj; }
    public String getIt36Subj2() { return it36Subj2; }
    public void setIt36Subj2(String it36Subj2) { this.it36Subj2 = it36Subj2; }
    public String getIt36Subj3() { return it36Subj3; }
    public void setIt36Subj3(String it36Subj3) { this.it36Subj3 = it36Subj3; }
    public String getIt36() { return it36; }
    public void setIt36(String it36) { this.it36 = it36; }
    public String getIt37Subj() { return it37Subj; }
    public void setIt37Subj(String it37Subj) { this.it37Subj = it37Subj; }
    public String getIt37Subj2() { return it37Subj2; }
    public void setIt37Subj2(String it37Subj2) { this.it37Subj2 = it37Subj2; }
    public String getIt37Subj3() { return it37Subj3; }
    public void setIt37Subj3(String it37Subj3) { this.it37Subj3 = it37Subj3; }
    public String getIt37() { return it37; }
    public void setIt37(String it37) { this.it37 = it37; }
    public String getIt38Subj() { return it38Subj; }
    public void setIt38Subj(String it38Subj) { this.it38Subj = it38Subj; }
    public String getIt38Subj2() { return it38Subj2; }
    public void setIt38Subj2(String it38Subj2) { this.it38Subj2 = it38Subj2; }
    public String getIt38Subj3() { return it38Subj3; }
    public void setIt38Subj3(String it38Subj3) { this.it38Subj3 = it38Subj3; }
    public String getIt38() { return it38; }
    public void setIt38(String it38) { this.it38 = it38; }
    public String getIt39Subj() { return it39Subj; }
    public void setIt39Subj(String it39Subj) { this.it39Subj = it39Subj; }
    public String getIt39Subj2() { return it39Subj2; }
    public void setIt39Subj2(String it39Subj2) { this.it39Subj2 = it39Subj2; }
    public String getIt39Subj3() { return it39Subj3; }
    public void setIt39Subj3(String it39Subj3) { this.it39Subj3 = it39Subj3; }
    public String getIt39() { return it39; }
    public void setIt39(String it39) { this.it39 = it39; }
    public String getIt40Subj() { return it40Subj; }
    public void setIt40Subj(String it40Subj) { this.it40Subj = it40Subj; }
    public String getIt40() { return it40; }
    public void setIt40(String it40) { this.it40 = it40; }
    public String getIt41Subj() { return it41Subj; }
    public void setIt41Subj(String it41Subj) { this.it41Subj = it41Subj; }
    public String getIt41() { return it41; }
    public void setIt41(String it41) { this.it41 = it41; }
    public String getIt42Subj() { return it42Subj; }
    public void setIt42Subj(String it42Subj) { this.it42Subj = it42Subj; }
    public String getIt42() { return it42; }
    public void setIt42(String it42) { this.it42 = it42; }
    public String getIt43Subj() { return it43Subj; }
    public void setIt43Subj(String it43Subj) { this.it43Subj = it43Subj; }
    public String getIt43() { return it43; }
    public void setIt43(String it43) { this.it43 = it43; }
    public String getIt44Subj() { return it44Subj; }
    public void setIt44Subj(String it44Subj) { this.it44Subj = it44Subj; }
    public String getIt44() { return it44; }
    public void setIt44(String it44) { this.it44 = it44; }
    public String getIt45Subj() { return it45Subj; }
    public void setIt45Subj(String it45Subj) { this.it45Subj = it45Subj; }
    public String getIt45() { return it45; }
    public void setIt45(String it45) { this.it45 = it45; }
    public String getIt46Subj() { return it46Subj; }
    public void setIt46Subj(String it46Subj) { this.it46Subj = it46Subj; }
    public String getIt46() { return it46; }
    public void setIt46(String it46) { this.it46 = it46; }
    public String getIt47Subj() { return it47Subj; }
    public void setIt47Subj(String it47Subj) { this.it47Subj = it47Subj; }
    public String getIt47() { return it47; }
    public void setIt47(String it47) { this.it47 = it47; }
    public String getIt48Subj() { return it48Subj; }
    public void setIt48Subj(String it48Subj) { this.it48Subj = it48Subj; }
    public String getIt48() { return it48; }
    public void setIt48(String it48) { this.it48 = it48; }
    public String getIt49Subj() { return it49Subj; }
    public void setIt49Subj(String it49Subj) { this.it49Subj = it49Subj; }
    public String getIt49() { return it49; }
    public void setIt49(String it49) { this.it49 = it49; }
    public String getIt50Subj() { return it50Subj; }
    public void setIt50Subj(String it50Subj) { this.it50Subj = it50Subj; }
    public String getIt50() { return it50; }
    public void setIt50(String it50) { this.it50 = it50; }
    public List<SpPartnerOrder> getPartnerOrders() { return partnerOrders; }
    public void setPartnerOrders(List<SpPartnerOrder> partnerOrders) { this.partnerOrders = partnerOrders; }
}
