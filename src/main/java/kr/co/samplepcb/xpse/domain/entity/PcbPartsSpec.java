package kr.co.samplepcb.xpse.domain.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "sp_pcb_parts_spec")
public class PcbPartsSpec {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parts_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_sp_pcb_parts_spec_parts"))
    private PcbParts pcbParts;

    @Column(name = "display_value", length = 255)
    private String displayValue;

    @Column(name = "attr_group", length = 255)
    private String attrGroup;

    @Column(name = "attr_name", length = 255)
    private String attrName;

    @Column(name = "attr_shortname", length = 255)
    private String attrShortname;

    // === getter / setter ===

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public PcbParts getPcbParts() { return pcbParts; }
    public void setPcbParts(PcbParts pcbParts) { this.pcbParts = pcbParts; }

    public String getDisplayValue() { return displayValue; }
    public void setDisplayValue(String displayValue) { this.displayValue = displayValue; }

    public String getAttrGroup() { return attrGroup; }
    public void setAttrGroup(String attrGroup) { this.attrGroup = attrGroup; }

    public String getAttrName() { return attrName; }
    public void setAttrName(String attrName) { this.attrName = attrName; }

    public String getAttrShortname() { return attrShortname; }
    public void setAttrShortname(String attrShortname) { this.attrShortname = attrShortname; }
}
