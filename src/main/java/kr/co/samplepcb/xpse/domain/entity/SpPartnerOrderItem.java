package kr.co.samplepcb.xpse.domain.entity;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "sp_partner_order_item",
        uniqueConstraints = @UniqueConstraint(name = "uk_sp_partner_order_item", columnNames = {"estimate_item_id", "partner_order_document_id"}))
public class SpPartnerOrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estimate_item_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_sp_partner_order_item_estimate"))
    private SpEstimateItem estimateItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partner_order_document_id",
            foreignKey = @ForeignKey(name = "fk_sp_partner_order_item_partner_doc"))
    private SpPartnerOrderDocument partnerOrderDocument;

    @Column(name = "mb_no", nullable = false)
    private int mbNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mb_no", insertable = false, updatable = false,
            foreignKey = @ForeignKey(name = "fk_sp_partner_order_item_member"))
    private G5Member member;

    @Lob
    @Column(name = "selected_price", columnDefinition = "text")
    private String selectedPrice;

    @Column(name = "status", length = 30)
    private String status;

    @Lob
    @Column(name = "memo", columnDefinition = "text")
    private String memo;

    @Column(name = "write_date", nullable = false)
    private Date writeDate;

    @Column(name = "date_code", length = 100)
    private String dateCode;

    @Column(name = "delivery_date")
    private Date deliveryDate;

    @Column(name = "modify_date", nullable = false)
    private Date modifyDate;

    // === getter / setter ===

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public SpEstimateItem getEstimateItem() { return estimateItem; }
    public void setEstimateItem(SpEstimateItem estimateItem) { this.estimateItem = estimateItem; }

    public SpPartnerOrderDocument getPartnerOrderDocument() { return partnerOrderDocument; }
    public void setPartnerOrderDocument(SpPartnerOrderDocument partnerOrderDocument) { this.partnerOrderDocument = partnerOrderDocument; }

    public int getMbNo() { return mbNo; }
    public void setMbNo(int mbNo) { this.mbNo = mbNo; }

    public G5Member getMember() { return member; }
    public void setMember(G5Member member) { this.member = member; }

    public String getSelectedPrice() { return selectedPrice; }
    public void setSelectedPrice(String selectedPrice) { this.selectedPrice = selectedPrice; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMemo() { return memo; }
    public void setMemo(String memo) { this.memo = memo; }

    public Date getWriteDate() { return writeDate; }
    public void setWriteDate(Date writeDate) { this.writeDate = writeDate; }

    public String getDateCode() { return dateCode; }
    public void setDateCode(String dateCode) { this.dateCode = dateCode; }

    public Date getDeliveryDate() { return deliveryDate; }
    public void setDeliveryDate(Date deliveryDate) { this.deliveryDate = deliveryDate; }

    public Date getModifyDate() { return modifyDate; }
    public void setModifyDate(Date modifyDate) { this.modifyDate = modifyDate; }
}
