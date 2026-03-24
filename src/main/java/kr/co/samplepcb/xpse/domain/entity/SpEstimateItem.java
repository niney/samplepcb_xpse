package kr.co.samplepcb.xpse.domain.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "sp_estimate_item")
public class SpEstimateItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estimate_document_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_sp_estimate_item_document"))
    private SpEstimateDocument estimateDocument;

    @Column(name = "pcb_part_doc_id", length = 20, nullable = false)
    private String pcbPartDocId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pcb_part_doc_id", referencedColumnName = "doc_id", insertable = false, updatable = false,
            foreignKey = @ForeignKey(name = "fk_sp_estimate_item_pcb_part"))
    private PcbParts pcbPart;

    @Column(name = "qty")
    private Integer qty;

    @Lob
    @Column(name = "analysis_meta", columnDefinition = "text")
    private String analysisMeta;

    @Lob
    @Column(name = "selected_price", columnDefinition = "text")
    private String selectedPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "selected_partner_estimate_item_id",
            foreignKey = @ForeignKey(name = "fk_sp_estimate_item_selected_partner"))
    private SpPartnerEstimateItem selectedPartnerEstimateItem;

    @Lob
    @Column(name = "confirmed_price", columnDefinition = "text")
    private String confirmedPrice;

    @Column(name = "write_date", nullable = false)
    private Date writeDate;

    @Column(name = "modify_date", nullable = false)
    private Date modifyDate;

    @OneToMany(mappedBy = "estimateItem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SpPartnerEstimateItem> partnerEstimateItems = new ArrayList<>();

    // === getter / setter ===

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public SpEstimateDocument getEstimateDocument() { return estimateDocument; }
    public void setEstimateDocument(SpEstimateDocument estimateDocument) { this.estimateDocument = estimateDocument; }

    public String getPcbPartDocId() { return pcbPartDocId; }
    public void setPcbPartDocId(String pcbPartDocId) { this.pcbPartDocId = pcbPartDocId; }

    public PcbParts getPcbPart() { return pcbPart; }
    public void setPcbPart(PcbParts pcbPart) { this.pcbPart = pcbPart; }

    public Integer getQty() { return qty; }
    public void setQty(Integer qty) { this.qty = qty; }

    public String getAnalysisMeta() { return analysisMeta; }
    public void setAnalysisMeta(String analysisMeta) { this.analysisMeta = analysisMeta; }

    public String getSelectedPrice() { return selectedPrice; }
    public void setSelectedPrice(String selectedPrice) { this.selectedPrice = selectedPrice; }

    public SpPartnerEstimateItem getSelectedPartnerEstimateItem() { return selectedPartnerEstimateItem; }
    public void setSelectedPartnerEstimateItem(SpPartnerEstimateItem selectedPartnerEstimateItem) { this.selectedPartnerEstimateItem = selectedPartnerEstimateItem; }

    public String getConfirmedPrice() { return confirmedPrice; }
    public void setConfirmedPrice(String confirmedPrice) { this.confirmedPrice = confirmedPrice; }

    public Date getWriteDate() { return writeDate; }
    public void setWriteDate(Date writeDate) { this.writeDate = writeDate; }

    public Date getModifyDate() { return modifyDate; }
    public void setModifyDate(Date modifyDate) { this.modifyDate = modifyDate; }

    public List<SpPartnerEstimateItem> getPartnerEstimateItems() { return partnerEstimateItems; }
    public void setPartnerEstimateItems(List<SpPartnerEstimateItem> partnerEstimateItems) { this.partnerEstimateItems = partnerEstimateItems; }
}
