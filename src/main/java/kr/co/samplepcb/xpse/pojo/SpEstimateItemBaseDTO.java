package kr.co.samplepcb.xpse.pojo;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "견적 항목 기본 DTO")
public class SpEstimateItemBaseDTO {

    @Schema(description = "항목 ID")
    private Long id;

    @Schema(description = "PCB 부품 doc_id")
    private String pcbPartDocId;

    @Schema(description = "수량")
    private Integer qty;

    @Schema(description = "선택된 협력사 견적 항목 ID")
    private Long selectedPartnerEstimateItemId;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getPcbPartDocId() { return pcbPartDocId; }
    public void setPcbPartDocId(String pcbPartDocId) { this.pcbPartDocId = pcbPartDocId; }
    public Integer getQty() { return qty; }
    public void setQty(Integer qty) { this.qty = qty; }
    public Long getSelectedPartnerEstimateItemId() { return selectedPartnerEstimateItemId; }
    public void setSelectedPartnerEstimateItemId(Long selectedPartnerEstimateItemId) { this.selectedPartnerEstimateItemId = selectedPartnerEstimateItemId; }
}
