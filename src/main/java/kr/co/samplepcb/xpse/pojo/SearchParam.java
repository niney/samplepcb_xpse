package kr.co.samplepcb.xpse.pojo;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "기본 검색 파라미터")
public class SearchParam {

    @Schema(description = "검색어")
    private String q;
    @Schema(description = "검색 필드")
    private String field;

    public String getQ() { return q; }
    public void setQ(String q) { this.q = q; }
    public String getField() { return field; }
    public void setField(String field) { this.field = field; }
}
