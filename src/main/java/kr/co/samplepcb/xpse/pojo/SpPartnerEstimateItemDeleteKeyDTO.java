package kr.co.samplepcb.xpse.pojo;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "협력사 견적 항목 삭제 요청")
public class SpPartnerEstimateItemDeleteKeyDTO {

    @Schema(description = "견적 항목 ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long estimateItemId;

    @Schema(description = "파트너 회원번호", requiredMode = Schema.RequiredMode.REQUIRED)
    private int mbNo;

    public Long getEstimateItemId() {
        return estimateItemId;
    }

    public void setEstimateItemId(Long estimateItemId) {
        this.estimateItemId = estimateItemId;
    }

    public int getMbNo() {
        return mbNo;
    }

    public void setMbNo(int mbNo) {
        this.mbNo = mbNo;
    }
}
