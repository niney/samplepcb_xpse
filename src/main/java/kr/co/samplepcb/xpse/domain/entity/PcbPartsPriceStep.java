package kr.co.samplepcb.xpse.domain.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "sp_pcb_parts_price_step")
public class PcbPartsPriceStep {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "price_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_sp_pcb_parts_price_step_price"))
    private PcbPartsPrice pcbPartsPrice;

    @Column(name = "break_quantity", nullable = false)
    private int breakQuantity;

    @Column(name = "unit_price", nullable = false)
    private int unitPrice;

    // === getter / setter ===

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public PcbPartsPrice getPcbPartsPrice() { return pcbPartsPrice; }
    public void setPcbPartsPrice(PcbPartsPrice pcbPartsPrice) { this.pcbPartsPrice = pcbPartsPrice; }

    public int getBreakQuantity() { return breakQuantity; }
    public void setBreakQuantity(int breakQuantity) { this.breakQuantity = breakQuantity; }

    public int getUnitPrice() { return unitPrice; }
    public void setUnitPrice(int unitPrice) { this.unitPrice = unitPrice; }
}
