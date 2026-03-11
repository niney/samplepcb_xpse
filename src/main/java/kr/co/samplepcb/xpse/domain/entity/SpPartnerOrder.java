package kr.co.samplepcb.xpse.domain.entity;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "sp_partner_order", uniqueConstraints = {
        @UniqueConstraint(name = "uk_sp_partner_order_it_partner", columnNames = {"it_id", "partner_mb_no"})
})
public class SpPartnerOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "it_id", length = 20, nullable = false)
    private String itId;

    @Column(name = "partner_mb_no", nullable = false)
    private int partnerMbNo;

    @Lob
    @Column(name = "meta_item", columnDefinition = "longtext")
    private String metaItem;

    @Column(name = "status", length = 30, nullable = false)
    private String status;

    @Column(name = "is_select_partner")
    private int isSelectPartner;

    @Column(name = "price")
    private Integer price;

    @Column(name = "forwarder", length = 30)
    private String forwarder;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "shipping")
    private Date shipping;

    @Column(name = "tracking", length = 30)
    private String tracking;

    @Column(name = "estimate_file1_subj", length = 50)
    private String estimateFile1Subj;

    @Column(name = "estimate_file1", length = 150)
    private String estimateFile1;

    @Lob
    @Column(name = "memo", columnDefinition = "text")
    private String memo;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "write_date", nullable = false)
    private Date writeDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "modify_date", nullable = false)
    private Date modifyDate;

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getItId() { return itId; }
    public void setItId(String itId) { this.itId = itId; }
    public int getPartnerMbNo() { return partnerMbNo; }
    public void setPartnerMbNo(int partnerMbNo) { this.partnerMbNo = partnerMbNo; }
    public String getMetaItem() { return metaItem; }
    public void setMetaItem(String metaItem) { this.metaItem = metaItem; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public int getIsSelectPartner() { return isSelectPartner; }
    public void setIsSelectPartner(int isSelectPartner) { this.isSelectPartner = isSelectPartner; }
    public Integer getPrice() { return price; }
    public void setPrice(Integer price) { this.price = price; }
    public String getForwarder() { return forwarder; }
    public void setForwarder(String forwarder) { this.forwarder = forwarder; }
    public Date getShipping() { return shipping; }
    public void setShipping(Date shipping) { this.shipping = shipping; }
    public String getTracking() { return tracking; }
    public void setTracking(String tracking) { this.tracking = tracking; }
    public String getEstimateFile1Subj() { return estimateFile1Subj; }
    public void setEstimateFile1Subj(String estimateFile1Subj) { this.estimateFile1Subj = estimateFile1Subj; }
    public String getEstimateFile1() { return estimateFile1; }
    public void setEstimateFile1(String estimateFile1) { this.estimateFile1 = estimateFile1; }
    public String getMemo() { return memo; }
    public void setMemo(String memo) { this.memo = memo; }
    public Date getWriteDate() { return writeDate; }
    public void setWriteDate(Date writeDate) { this.writeDate = writeDate; }
    public Date getModifyDate() { return modifyDate; }
    public void setModifyDate(Date modifyDate) { this.modifyDate = modifyDate; }
}
