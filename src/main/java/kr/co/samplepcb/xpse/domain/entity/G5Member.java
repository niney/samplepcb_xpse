package kr.co.samplepcb.xpse.domain.entity;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "g5_member")
public class G5Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mb_no")
    private int mbNo;

    @Column(name = "mb_id", unique = true)
    private String mbId;

    @Column(name = "mb_password")
    private String mbPassword;

    @Column(name = "mb_name")
    private String mbName;

    @Column(name = "mb_nick")
    private String mbNick;

    @Column(name = "mb_nick_date")
    private Date mbNickDate;

    @Column(name = "mb_email")
    private String mbEmail;

    @Column(name = "mb_homepage")
    private String mbHomepage;

    @Column(name = "mb_level")
    private int mbLevel;

    @Column(name = "mb_sex", length = 1)
    private String mbSex;

    @Column(name = "mb_birth")
    private String mbBirth;

    @Column(name = "mb_tel")
    private String mbTel;

    @Column(name = "mb_hp")
    private String mbHp;

    @Column(name = "mb_certify", length = 20)
    private String mbCertify;

    @Column(name = "mb_adult")
    private int mbAdult;

    @Column(name = "mb_dupinfo")
    private String mbDupinfo;

    @Column(name = "mb_zip1", length = 3)
    private String mbZip1;

    @Column(name = "mb_zip2", length = 3)
    private String mbZip2;

    @Column(name = "mb_addr1")
    private String mbAddr1;

    @Column(name = "mb_addr2")
    private String mbAddr2;

    @Column(name = "mb_addr3")
    private String mbAddr3;

    @Column(name = "mb_addr_jibeon")
    private String mbAddrJibeon;

    @Lob
    @Column(name = "mb_signature", columnDefinition = "text")
    private String mbSignature;

    @Column(name = "mb_recommend")
    private String mbRecommend;

    @Column(name = "mb_point")
    private int mbPoint;

    @Column(name = "mb_today_login")
    private Date mbTodayLogin;

    @Column(name = "mb_login_ip")
    private String mbLoginIp;

    @Column(name = "mb_datetime")
    private Date mbDatetime;

    @Column(name = "mb_ip")
    private String mbIp;

    @Column(name = "mb_leave_date", length = 8)
    private String mbLeaveDate;

    @Column(name = "mb_intercept_date", length = 8)
    private String mbInterceptDate;

    @Column(name = "mb_email_certify")
    private Date mbEmailCertify;

    @Column(name = "mb_email_certify2")
    private String mbEmailCertify2;

    @Lob
    @Column(name = "mb_memo", columnDefinition = "text")
    private String mbMemo;

    @Column(name = "mb_lost_certify")
    private String mbLostCertify;

    @Column(name = "mb_mailling")
    private int mbMailling;

    @Column(name = "mb_sms")
    private int mbSms;

    @Column(name = "mb_open")
    private int mbOpen;

    @Column(name = "mb_open_date")
    private Date mbOpenDate;

    @Lob
    @Column(name = "mb_profile", columnDefinition = "text")
    private String mbProfile;

    @Column(name = "mb_memo_call")
    private String mbMemoCall;

    @Column(name = "mb_partner_auth")
    private int mbPartnerAuth;

    @Column(name = "mb_user_bank_name", length = 10)
    private String mbUserBankName;

    @Column(name = "mb_user_account_number", length = 30)
    private String mbUserAccountNumber;

    @Column(name = "mb_user_account_holder", length = 10)
    private String mbUserAccountHolder;

    @Column(name = "mb_company_bank_name", length = 10)
    private String mbCompanyBankName;

    @Column(name = "mb_company_account_number", length = 30)
    private String mbCompanyAccountNumber;

    @Column(name = "mb_company_account_holder", length = 10)
    private String mbCompanyAccountHolder;

    @Column(name = "mb_1")
    private String mb1;

    @Column(name = "mb_2")
    private String mb2;

    @Column(name = "mb_3")
    private String mb3;

    @Column(name = "mb_4")
    private String mb4;

    @Column(name = "mb_5")
    private String mb5;

    @Column(name = "mb_6")
    private String mb6;

    @Column(name = "mb_7")
    private String mb7;

    @Column(name = "mb_8")
    private String mb8;

    @Column(name = "mb_9")
    private String mb9;

    @Column(name = "mb_10")
    private String mb10;

    @Column(name = "mb_11", length = 100)
    private String mb11;

    @Column(name = "mb_12", length = 100)
    private String mb12;

    @Column(name = "mb_13", length = 100)
    private String mb13;

    @Column(name = "mb_14", length = 100)
    private String mb14;

    @Column(name = "mb_15", length = 100)
    private String mb15;

    @Column(name = "mb_16", length = 100)
    private String mb16;

    @Column(name = "mb_17", length = 100)
    private String mb17;

    public int getMbNo() { return mbNo; }
    public void setMbNo(int mbNo) { this.mbNo = mbNo; }
    public String getMbId() { return mbId; }
    public void setMbId(String mbId) { this.mbId = mbId; }
    public String getMbPassword() { return mbPassword; }
    public void setMbPassword(String mbPassword) { this.mbPassword = mbPassword; }
    public String getMbName() { return mbName; }
    public void setMbName(String mbName) { this.mbName = mbName; }
    public String getMbNick() { return mbNick; }
    public void setMbNick(String mbNick) { this.mbNick = mbNick; }
    public Date getMbNickDate() { return mbNickDate; }
    public void setMbNickDate(Date mbNickDate) { this.mbNickDate = mbNickDate; }
    public String getMbEmail() { return mbEmail; }
    public void setMbEmail(String mbEmail) { this.mbEmail = mbEmail; }
    public String getMbHomepage() { return mbHomepage; }
    public void setMbHomepage(String mbHomepage) { this.mbHomepage = mbHomepage; }
    public int getMbLevel() { return mbLevel; }
    public void setMbLevel(int mbLevel) { this.mbLevel = mbLevel; }
    public String getMbSex() { return mbSex; }
    public void setMbSex(String mbSex) { this.mbSex = mbSex; }
    public String getMbBirth() { return mbBirth; }
    public void setMbBirth(String mbBirth) { this.mbBirth = mbBirth; }
    public String getMbTel() { return mbTel; }
    public void setMbTel(String mbTel) { this.mbTel = mbTel; }
    public String getMbHp() { return mbHp; }
    public void setMbHp(String mbHp) { this.mbHp = mbHp; }
    public String getMbCertify() { return mbCertify; }
    public void setMbCertify(String mbCertify) { this.mbCertify = mbCertify; }
    public int getMbAdult() { return mbAdult; }
    public void setMbAdult(int mbAdult) { this.mbAdult = mbAdult; }
    public String getMbDupinfo() { return mbDupinfo; }
    public void setMbDupinfo(String mbDupinfo) { this.mbDupinfo = mbDupinfo; }
    public String getMbZip1() { return mbZip1; }
    public void setMbZip1(String mbZip1) { this.mbZip1 = mbZip1; }
    public String getMbZip2() { return mbZip2; }
    public void setMbZip2(String mbZip2) { this.mbZip2 = mbZip2; }
    public String getMbAddr1() { return mbAddr1; }
    public void setMbAddr1(String mbAddr1) { this.mbAddr1 = mbAddr1; }
    public String getMbAddr2() { return mbAddr2; }
    public void setMbAddr2(String mbAddr2) { this.mbAddr2 = mbAddr2; }
    public String getMbAddr3() { return mbAddr3; }
    public void setMbAddr3(String mbAddr3) { this.mbAddr3 = mbAddr3; }
    public String getMbAddrJibeon() { return mbAddrJibeon; }
    public void setMbAddrJibeon(String mbAddrJibeon) { this.mbAddrJibeon = mbAddrJibeon; }
    public String getMbSignature() { return mbSignature; }
    public void setMbSignature(String mbSignature) { this.mbSignature = mbSignature; }
    public String getMbRecommend() { return mbRecommend; }
    public void setMbRecommend(String mbRecommend) { this.mbRecommend = mbRecommend; }
    public int getMbPoint() { return mbPoint; }
    public void setMbPoint(int mbPoint) { this.mbPoint = mbPoint; }
    public Date getMbTodayLogin() { return mbTodayLogin; }
    public void setMbTodayLogin(Date mbTodayLogin) { this.mbTodayLogin = mbTodayLogin; }
    public String getMbLoginIp() { return mbLoginIp; }
    public void setMbLoginIp(String mbLoginIp) { this.mbLoginIp = mbLoginIp; }
    public Date getMbDatetime() { return mbDatetime; }
    public void setMbDatetime(Date mbDatetime) { this.mbDatetime = mbDatetime; }
    public String getMbIp() { return mbIp; }
    public void setMbIp(String mbIp) { this.mbIp = mbIp; }
    public String getMbLeaveDate() { return mbLeaveDate; }
    public void setMbLeaveDate(String mbLeaveDate) { this.mbLeaveDate = mbLeaveDate; }
    public String getMbInterceptDate() { return mbInterceptDate; }
    public void setMbInterceptDate(String mbInterceptDate) { this.mbInterceptDate = mbInterceptDate; }
    public Date getMbEmailCertify() { return mbEmailCertify; }
    public void setMbEmailCertify(Date mbEmailCertify) { this.mbEmailCertify = mbEmailCertify; }
    public String getMbEmailCertify2() { return mbEmailCertify2; }
    public void setMbEmailCertify2(String mbEmailCertify2) { this.mbEmailCertify2 = mbEmailCertify2; }
    public String getMbMemo() { return mbMemo; }
    public void setMbMemo(String mbMemo) { this.mbMemo = mbMemo; }
    public String getMbLostCertify() { return mbLostCertify; }
    public void setMbLostCertify(String mbLostCertify) { this.mbLostCertify = mbLostCertify; }
    public int getMbMailling() { return mbMailling; }
    public void setMbMailling(int mbMailling) { this.mbMailling = mbMailling; }
    public int getMbSms() { return mbSms; }
    public void setMbSms(int mbSms) { this.mbSms = mbSms; }
    public int getMbOpen() { return mbOpen; }
    public void setMbOpen(int mbOpen) { this.mbOpen = mbOpen; }
    public Date getMbOpenDate() { return mbOpenDate; }
    public void setMbOpenDate(Date mbOpenDate) { this.mbOpenDate = mbOpenDate; }
    public String getMbProfile() { return mbProfile; }
    public void setMbProfile(String mbProfile) { this.mbProfile = mbProfile; }
    public String getMbMemoCall() { return mbMemoCall; }
    public void setMbMemoCall(String mbMemoCall) { this.mbMemoCall = mbMemoCall; }
    public int getMbPartnerAuth() { return mbPartnerAuth; }
    public void setMbPartnerAuth(int mbPartnerAuth) { this.mbPartnerAuth = mbPartnerAuth; }
    public String getMbUserBankName() { return mbUserBankName; }
    public void setMbUserBankName(String mbUserBankName) { this.mbUserBankName = mbUserBankName; }
    public String getMbUserAccountNumber() { return mbUserAccountNumber; }
    public void setMbUserAccountNumber(String mbUserAccountNumber) { this.mbUserAccountNumber = mbUserAccountNumber; }
    public String getMbUserAccountHolder() { return mbUserAccountHolder; }
    public void setMbUserAccountHolder(String mbUserAccountHolder) { this.mbUserAccountHolder = mbUserAccountHolder; }
    public String getMbCompanyBankName() { return mbCompanyBankName; }
    public void setMbCompanyBankName(String mbCompanyBankName) { this.mbCompanyBankName = mbCompanyBankName; }
    public String getMbCompanyAccountNumber() { return mbCompanyAccountNumber; }
    public void setMbCompanyAccountNumber(String mbCompanyAccountNumber) { this.mbCompanyAccountNumber = mbCompanyAccountNumber; }
    public String getMbCompanyAccountHolder() { return mbCompanyAccountHolder; }
    public void setMbCompanyAccountHolder(String mbCompanyAccountHolder) { this.mbCompanyAccountHolder = mbCompanyAccountHolder; }
    public String getMb1() { return mb1; }
    public void setMb1(String mb1) { this.mb1 = mb1; }
    public String getMb2() { return mb2; }
    public void setMb2(String mb2) { this.mb2 = mb2; }
    public String getMb3() { return mb3; }
    public void setMb3(String mb3) { this.mb3 = mb3; }
    public String getMb4() { return mb4; }
    public void setMb4(String mb4) { this.mb4 = mb4; }
    public String getMb5() { return mb5; }
    public void setMb5(String mb5) { this.mb5 = mb5; }
    public String getMb6() { return mb6; }
    public void setMb6(String mb6) { this.mb6 = mb6; }
    public String getMb7() { return mb7; }
    public void setMb7(String mb7) { this.mb7 = mb7; }
    public String getMb8() { return mb8; }
    public void setMb8(String mb8) { this.mb8 = mb8; }
    public String getMb9() { return mb9; }
    public void setMb9(String mb9) { this.mb9 = mb9; }
    public String getMb10() { return mb10; }
    public void setMb10(String mb10) { this.mb10 = mb10; }
    public String getMb11() { return mb11; }
    public void setMb11(String mb11) { this.mb11 = mb11; }
    public String getMb12() { return mb12; }
    public void setMb12(String mb12) { this.mb12 = mb12; }
    public String getMb13() { return mb13; }
    public void setMb13(String mb13) { this.mb13 = mb13; }
    public String getMb14() { return mb14; }
    public void setMb14(String mb14) { this.mb14 = mb14; }
    public String getMb15() { return mb15; }
    public void setMb15(String mb15) { this.mb15 = mb15; }
    public String getMb16() { return mb16; }
    public void setMb16(String mb16) { this.mb16 = mb16; }
    public String getMb17() { return mb17; }
    public void setMb17(String mb17) { this.mb17 = mb17; }
}
