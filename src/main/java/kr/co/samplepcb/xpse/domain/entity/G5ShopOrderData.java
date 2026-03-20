package kr.co.samplepcb.xpse.domain.entity;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "g5_shop_order_data")
@IdClass(G5ShopOrderDataId.class)
public class G5ShopOrderData {

    @Id
    @Column(name = "od_id")
    private long odId;

    @Id
    @Column(name = "cart_id")
    private long cartId;

    @Column(name = "mb_id", length = 20)
    private String mbId;

    @Column(name = "dt_pg")
    private String dtPg;

    @Lob
    @Column(name = "dt_data", columnDefinition = "text")
    private String dtData;

    @Column(name = "dt_time")
    private Date dtTime;

    public long getOdId() { return odId; }
    public void setOdId(long odId) { this.odId = odId; }
    public long getCartId() { return cartId; }
    public void setCartId(long cartId) { this.cartId = cartId; }
    public String getMbId() { return mbId; }
    public void setMbId(String mbId) { this.mbId = mbId; }
    public String getDtPg() { return dtPg; }
    public void setDtPg(String dtPg) { this.dtPg = dtPg; }
    public String getDtData() { return dtData; }
    public void setDtData(String dtData) { this.dtData = dtData; }
    public Date getDtTime() { return dtTime; }
    public void setDtTime(Date dtTime) { this.dtTime = dtTime; }
}
