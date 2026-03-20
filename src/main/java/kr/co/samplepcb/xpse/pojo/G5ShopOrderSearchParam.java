package kr.co.samplepcb.xpse.pojo;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "G5 주문 검색 파라미터")
public class G5ShopOrderSearchParam extends SearchParam {

    @Schema(description = "주문 상태")
    private String odStatus;
    @Schema(description = "회원 ID")
    private String mbId;
    @Schema(description = "카테고리 ID", defaultValue = "41")
    private String caId = "41";

    public String getOdStatus() { return odStatus; }
    public void setOdStatus(String odStatus) { this.odStatus = odStatus; }
    public String getMbId() { return mbId; }
    public void setMbId(String mbId) { this.mbId = mbId; }
    public String getCaId() { return caId; }
    public void setCaId(String caId) { this.caId = caId; }
}
