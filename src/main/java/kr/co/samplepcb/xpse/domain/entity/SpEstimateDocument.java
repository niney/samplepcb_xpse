package kr.co.samplepcb.xpse.domain.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "sp_estimate_document")
public class SpEstimateDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "it_id", length = 20, nullable = false)
    private String itId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "it_id", referencedColumnName = "it_id", insertable = false, updatable = false,
            foreignKey = @ForeignKey(name = "fk_sp_estimate_document_it_id"))
    private G5ShopItem shopItem;

    @Column(name = "status", length = 30)
    private String status;

    @Column(name = "expected_delivery", length = 100)
    private String expectedDelivery;

    @Column(name = "shipping_fee")
    private Integer shippingFee;

    @Column(name = "management_fee")
    private Integer managementFee;

    @Column(name = "total_amount")
    private Integer totalAmount;

    @Column(name = "final_amount")
    private Integer finalAmount;

    @Column(name = "memo", columnDefinition = "TEXT")
    private String memo;

    @Column(name = "global_margin_rate")
    private Integer globalMarginRate;

    @Column(name = "write_date")
    private Date writeDate;

    @Column(name = "modify_date")
    private Date modifyDate;

    @OneToMany(mappedBy = "estimateDocument", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SpEstimateItem> items = new ArrayList<>();

    // === getter / setter ===

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getItId() { return itId; }
    public void setItId(String itId) { this.itId = itId; }

    public G5ShopItem getShopItem() { return shopItem; }
    public void setShopItem(G5ShopItem shopItem) { this.shopItem = shopItem; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getExpectedDelivery() { return expectedDelivery; }
    public void setExpectedDelivery(String expectedDelivery) { this.expectedDelivery = expectedDelivery; }

    public Integer getShippingFee() { return shippingFee; }
    public void setShippingFee(Integer shippingFee) { this.shippingFee = shippingFee; }

    public Integer getManagementFee() { return managementFee; }
    public void setManagementFee(Integer managementFee) { this.managementFee = managementFee; }

    public Integer getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Integer totalAmount) { this.totalAmount = totalAmount; }

    public Integer getFinalAmount() { return finalAmount; }
    public void setFinalAmount(Integer finalAmount) { this.finalAmount = finalAmount; }

    public String getMemo() { return memo; }
    public void setMemo(String memo) { this.memo = memo; }

    public Integer getGlobalMarginRate() { return globalMarginRate; }
    public void setGlobalMarginRate(Integer globalMarginRate) { this.globalMarginRate = globalMarginRate; }

    public Date getWriteDate() { return writeDate; }
    public void setWriteDate(Date writeDate) { this.writeDate = writeDate; }

    public Date getModifyDate() { return modifyDate; }
    public void setModifyDate(Date modifyDate) { this.modifyDate = modifyDate; }

    public List<SpEstimateItem> getItems() { return items; }
    public void setItems(List<SpEstimateItem> items) { this.items = items; }
}
