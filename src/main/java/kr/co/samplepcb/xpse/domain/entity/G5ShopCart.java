package kr.co.samplepcb.xpse.domain.entity;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "g5_shop_cart")
public class G5ShopCart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ct_id")
    private int ctId;

    @Column(name = "od_id", nullable = false)
    private long odId;

    @Column(name = "mb_id")
    private String mbId;

    @Column(name = "it_id", length = 20)
    private String itId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "it_id", referencedColumnName = "it_id", insertable = false, updatable = false)
    private G5ShopItem shopItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mb_id", referencedColumnName = "mb_id", insertable = false, updatable = false)
    private G5Member member;

    @Column(name = "it_name")
    private String itName;

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

    @Column(name = "ct_status")
    private String ctStatus;

    @Lob
    @Column(name = "ct_history", columnDefinition = "text")
    private String ctHistory;

    @Column(name = "ct_price")
    private int ctPrice;

    @Column(name = "ct_point")
    private int ctPoint;

    @Column(name = "cp_price")
    private int cpPrice;

    @Column(name = "ct_point_use")
    private int ctPointUse;

    @Column(name = "ct_stock_use")
    private int ctStockUse;

    @Column(name = "ct_option")
    private String ctOption;

    @Column(name = "ct_qty")
    private int ctQty;

    @Column(name = "ct_notax")
    private int ctNotax;

    @Column(name = "io_id")
    private String ioId;

    @Column(name = "io_type")
    private int ioType;

    @Column(name = "io_price")
    private int ioPrice;

    @Column(name = "ct_time")
    private Date ctTime;

    @Column(name = "ct_ip", length = 25)
    private String ctIp;

    @Column(name = "ct_send_cost")
    private int ctSendCost;

    @Column(name = "ct_direct")
    private int ctDirect;

    @Column(name = "ct_select")
    private int ctSelect;

    @Column(name = "ct_select_time")
    private Date ctSelectTime;

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
    public String getCtStatus() { return ctStatus; }
    public void setCtStatus(String ctStatus) { this.ctStatus = ctStatus; }
    public String getCtHistory() { return ctHistory; }
    public void setCtHistory(String ctHistory) { this.ctHistory = ctHistory; }
    public int getCtPrice() { return ctPrice; }
    public void setCtPrice(int ctPrice) { this.ctPrice = ctPrice; }
    public int getCtPoint() { return ctPoint; }
    public void setCtPoint(int ctPoint) { this.ctPoint = ctPoint; }
    public int getCpPrice() { return cpPrice; }
    public void setCpPrice(int cpPrice) { this.cpPrice = cpPrice; }
    public int getCtPointUse() { return ctPointUse; }
    public void setCtPointUse(int ctPointUse) { this.ctPointUse = ctPointUse; }
    public int getCtStockUse() { return ctStockUse; }
    public void setCtStockUse(int ctStockUse) { this.ctStockUse = ctStockUse; }
    public String getCtOption() { return ctOption; }
    public void setCtOption(String ctOption) { this.ctOption = ctOption; }
    public int getCtQty() { return ctQty; }
    public void setCtQty(int ctQty) { this.ctQty = ctQty; }
    public int getCtNotax() { return ctNotax; }
    public void setCtNotax(int ctNotax) { this.ctNotax = ctNotax; }
    public String getIoId() { return ioId; }
    public void setIoId(String ioId) { this.ioId = ioId; }
    public int getIoType() { return ioType; }
    public void setIoType(int ioType) { this.ioType = ioType; }
    public int getIoPrice() { return ioPrice; }
    public void setIoPrice(int ioPrice) { this.ioPrice = ioPrice; }
    public Date getCtTime() { return ctTime; }
    public void setCtTime(Date ctTime) { this.ctTime = ctTime; }
    public String getCtIp() { return ctIp; }
    public void setCtIp(String ctIp) { this.ctIp = ctIp; }
    public int getCtSendCost() { return ctSendCost; }
    public void setCtSendCost(int ctSendCost) { this.ctSendCost = ctSendCost; }
    public int getCtDirect() { return ctDirect; }
    public void setCtDirect(int ctDirect) { this.ctDirect = ctDirect; }
    public int getCtSelect() { return ctSelect; }
    public void setCtSelect(int ctSelect) { this.ctSelect = ctSelect; }
    public Date getCtSelectTime() { return ctSelectTime; }
    public void setCtSelectTime(Date ctSelectTime) { this.ctSelectTime = ctSelectTime; }
    public G5ShopItem getShopItem() { return shopItem; }
    public void setShopItem(G5ShopItem shopItem) { this.shopItem = shopItem; }
    public G5Member getMember() { return member; }
    public void setMember(G5Member member) { this.member = member; }
}
