package kr.co.samplepcb.xpse.domain.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "g5_shop_order_address")
public class G5ShopOrderAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ad_id")
    private int adId;

    @Column(name = "mb_id")
    private String mbId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mb_id", referencedColumnName = "mb_id", insertable = false, updatable = false)
    private G5Member member;

    @Column(name = "ad_subject")
    private String adSubject;

    @Column(name = "ad_default")
    private int adDefault;

    @Column(name = "ad_name")
    private String adName;

    @Column(name = "ad_tel")
    private String adTel;

    @Column(name = "ad_hp")
    private String adHp;

    @Column(name = "ad_zip1", length = 3)
    private String adZip1;

    @Column(name = "ad_zip2", length = 3)
    private String adZip2;

    @Column(name = "ad_addr1")
    private String adAddr1;

    @Column(name = "ad_addr2")
    private String adAddr2;

    @Column(name = "ad_addr3")
    private String adAddr3;

    @Column(name = "ad_jibeon")
    private String adJibeon;

    public int getAdId() { return adId; }
    public void setAdId(int adId) { this.adId = adId; }
    public String getMbId() { return mbId; }
    public void setMbId(String mbId) { this.mbId = mbId; }
    public G5Member getMember() { return member; }
    public void setMember(G5Member member) { this.member = member; }
    public String getAdSubject() { return adSubject; }
    public void setAdSubject(String adSubject) { this.adSubject = adSubject; }
    public int getAdDefault() { return adDefault; }
    public void setAdDefault(int adDefault) { this.adDefault = adDefault; }
    public String getAdName() { return adName; }
    public void setAdName(String adName) { this.adName = adName; }
    public String getAdTel() { return adTel; }
    public void setAdTel(String adTel) { this.adTel = adTel; }
    public String getAdHp() { return adHp; }
    public void setAdHp(String adHp) { this.adHp = adHp; }
    public String getAdZip1() { return adZip1; }
    public void setAdZip1(String adZip1) { this.adZip1 = adZip1; }
    public String getAdZip2() { return adZip2; }
    public void setAdZip2(String adZip2) { this.adZip2 = adZip2; }
    public String getAdAddr1() { return adAddr1; }
    public void setAdAddr1(String adAddr1) { this.adAddr1 = adAddr1; }
    public String getAdAddr2() { return adAddr2; }
    public void setAdAddr2(String adAddr2) { this.adAddr2 = adAddr2; }
    public String getAdAddr3() { return adAddr3; }
    public void setAdAddr3(String adAddr3) { this.adAddr3 = adAddr3; }
    public String getAdJibeon() { return adJibeon; }
    public void setAdJibeon(String adJibeon) { this.adJibeon = adJibeon; }
}
