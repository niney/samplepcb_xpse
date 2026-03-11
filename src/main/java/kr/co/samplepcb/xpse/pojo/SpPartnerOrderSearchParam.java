package kr.co.samplepcb.xpse.pojo;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "협력사 주문 검색 파라미터")
public class SpPartnerOrderSearchParam extends SearchParam {

    @Schema(description = "아이템 ID")
    private String itId;
    @Schema(description = "파트너 회원번호")
    private Integer partnerMbNo;
    @Schema(description = "주문 상태")
    private String status;

    public String getItId() { return itId; }
    public void setItId(String itId) { this.itId = itId; }
    public Integer getPartnerMbNo() { return partnerMbNo; }
    public void setPartnerMbNo(Integer partnerMbNo) { this.partnerMbNo = partnerMbNo; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
