package kr.co.samplepcb.xpse.domain.entity;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "g5_shop_order_delete")
public class G5ShopOrderDelete {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "de_id")
    private int deId;

    @Column(name = "de_key")
    private String deKey;

    @Lob
    @Column(name = "de_data", columnDefinition = "longtext")
    private String deData;

    @Column(name = "mb_id", length = 20)
    private String mbId;

    @Column(name = "de_ip")
    private String deIp;

    @Column(name = "de_datetime")
    private Date deDatetime;

    public int getDeId() { return deId; }
    public void setDeId(int deId) { this.deId = deId; }
    public String getDeKey() { return deKey; }
    public void setDeKey(String deKey) { this.deKey = deKey; }
    public String getDeData() { return deData; }
    public void setDeData(String deData) { this.deData = deData; }
    public String getMbId() { return mbId; }
    public void setMbId(String mbId) { this.mbId = mbId; }
    public String getDeIp() { return deIp; }
    public void setDeIp(String deIp) { this.deIp = deIp; }
    public Date getDeDatetime() { return deDatetime; }
    public void setDeDatetime(Date deDatetime) { this.deDatetime = deDatetime; }
}
