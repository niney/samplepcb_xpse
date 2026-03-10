package kr.co.samplepcb.xpse.pojo;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "주문 검색 파라미터")
public class SpOrderSearchParam extends SearchParam {

    @Schema(description = "장바구니 상태")
    private String ctStatus;
    @Schema(description = "카테고리 ID")
    private String caId;

    public String getCtStatus() { return ctStatus; }
    public void setCtStatus(String ctStatus) { this.ctStatus = ctStatus; }
    public String getCaId() { return caId; }
    public void setCaId(String caId) { this.caId = caId; }
}
