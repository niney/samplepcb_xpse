package kr.co.samplepcb.xpse.domain.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "sp_partner_order_document",
        uniqueConstraints = @UniqueConstraint(name = "uk_sp_partner_order_doc", columnNames = {"estimate_document_id", "mb_no"}))
public class SpPartnerOrderDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estimate_document_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_sp_partner_order_doc_document"))
    private SpEstimateDocument estimateDocument;

    @Column(name = "mb_no", nullable = false)
    private int mbNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mb_no", insertable = false, updatable = false,
            foreignKey = @ForeignKey(name = "fk_sp_partner_order_doc_member"))
    private G5Member member;

    @Column(name = "status", length = 30)
    private String status;

    @Column(name = "order_price")
    private Integer orderPrice;

    @Lob
    @Column(name = "memo", columnDefinition = "text")
    private String memo;

    @Column(name = "delivery_date")
    private Date deliveryDate;

    @Column(name = "write_date", nullable = false)
    private Date writeDate;

    @Column(name = "modify_date", nullable = false)
    private Date modifyDate;

    @OneToMany(mappedBy = "partnerOrderDocument", cascade = CascadeType.ALL)
    private List<SpPartnerOrderItem> partnerOrderItems = new ArrayList<>();

    // === getter / setter ===

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public SpEstimateDocument getEstimateDocument() { return estimateDocument; }
    public void setEstimateDocument(SpEstimateDocument estimateDocument) { this.estimateDocument = estimateDocument; }

    public int getMbNo() { return mbNo; }
    public void setMbNo(int mbNo) { this.mbNo = mbNo; }

    public G5Member getMember() { return member; }
    public void setMember(G5Member member) { this.member = member; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getOrderPrice() { return orderPrice; }
    public void setOrderPrice(Integer orderPrice) { this.orderPrice = orderPrice; }

    public String getMemo() { return memo; }
    public void setMemo(String memo) { this.memo = memo; }

    public Date getDeliveryDate() { return deliveryDate; }
    public void setDeliveryDate(Date deliveryDate) { this.deliveryDate = deliveryDate; }

    public Date getWriteDate() { return writeDate; }
    public void setWriteDate(Date writeDate) { this.writeDate = writeDate; }

    public Date getModifyDate() { return modifyDate; }
    public void setModifyDate(Date modifyDate) { this.modifyDate = modifyDate; }

    public List<SpPartnerOrderItem> getPartnerOrderItems() { return partnerOrderItems; }
    public void setPartnerOrderItems(List<SpPartnerOrderItem> partnerOrderItems) { this.partnerOrderItems = partnerOrderItems; }
}
