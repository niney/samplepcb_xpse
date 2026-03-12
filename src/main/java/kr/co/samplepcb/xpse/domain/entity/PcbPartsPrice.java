package kr.co.samplepcb.xpse.domain.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "sp_pcb_parts_price")
public class PcbPartsPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parts_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_sp_pcb_parts_price_parts"))
    private PcbParts pcbParts;

    @Column(name = "distributor", length = 255)
    private String distributor;

    @Column(name = "sku", length = 255)
    private String sku;

    @Column(name = "stock", nullable = false)
    private int stock;

    @Column(name = "moq", nullable = false)
    private int moq;

    @Column(name = "pkg", length = 100)
    private String pkg;

    @Column(name = "updated_date")
    private Date updatedDate;

    @OneToMany(mappedBy = "pcbPartsPrice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PcbPartsPriceStep> priceSteps = new ArrayList<>();

    // === getter / setter ===

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public PcbParts getPcbParts() { return pcbParts; }
    public void setPcbParts(PcbParts pcbParts) { this.pcbParts = pcbParts; }

    public String getDistributor() { return distributor; }
    public void setDistributor(String distributor) { this.distributor = distributor; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    public int getMoq() { return moq; }
    public void setMoq(int moq) { this.moq = moq; }

    public String getPkg() { return pkg; }
    public void setPkg(String pkg) { this.pkg = pkg; }

    public Date getUpdatedDate() { return updatedDate; }
    public void setUpdatedDate(Date updatedDate) { this.updatedDate = updatedDate; }

    public List<PcbPartsPriceStep> getPriceSteps() { return priceSteps; }
    public void setPriceSteps(List<PcbPartsPriceStep> priceSteps) { this.priceSteps = priceSteps; }
}
