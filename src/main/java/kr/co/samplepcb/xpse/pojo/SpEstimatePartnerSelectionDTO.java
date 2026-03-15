package kr.co.samplepcb.xpse.pojo;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "협력사 견적 다중 선택 요청")
public class SpEstimatePartnerSelectionDTO {

    @Schema(description = "견적 항목 ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long estimateItemId;

    @Schema(description = "선택할 협력사 견적 항목 ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long partnerEstimateItemId;

    public Long getEstimateItemId() {
        return estimateItemId;
    }

    public void setEstimateItemId(Long estimateItemId) {
        this.estimateItemId = estimateItemId;
    }

    public Long getPartnerEstimateItemId() {
        return partnerEstimateItemId;
    }

    public void setPartnerEstimateItemId(Long partnerEstimateItemId) {
        this.partnerEstimateItemId = partnerEstimateItemId;
    }
}
