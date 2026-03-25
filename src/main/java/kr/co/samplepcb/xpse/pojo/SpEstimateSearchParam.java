package kr.co.samplepcb.xpse.pojo;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "견적서 검색 파라미터")
public class SpEstimateSearchParam extends SearchParam {

    @Schema(description = "아이템 ID")
    private String itId;

    @Schema(description = "상태 (order / rfq)")
    private String status;

    public String getItId() { return itId; }
    public void setItId(String itId) { this.itId = itId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Schema(description = "협력사 mbNo 필터 (JWT에서 자동 세팅)", hidden = true)
    private Long partnerMbNo;

    public Long getPartnerMbNo() { return partnerMbNo; }
    public void setPartnerMbNo(Long partnerMbNo) { this.partnerMbNo = partnerMbNo; }
}
