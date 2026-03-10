package kr.co.samplepcb.xpse.pojo;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "PCB 컬럼 검색 조건")
public class PcbColumnSearchVM {

    @Schema(description = "ID")
    private String id;
    @Schema(description = "컬럼명")
    private String colName;
    @Schema(description = "대상 구분")
    private Integer target;
    @Schema(description = "검색 점수")
    private Double queryScore;
    @Schema(description = "검색 컬럼명")
    private String queryColName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getColName() {
        return colName;
    }

    public void setColName(String colName) {
        this.colName = colName;
    }

    public Integer getTarget() {
        return target;
    }

    public void setTarget(Integer target) {
        this.target = target;
    }

    public Double getQueryScore() {
        return queryScore;
    }

    public void setQueryScore(Double queryScore) {
        this.queryScore = queryScore;
    }

    public String getQueryColName() {
        return queryColName;
    }

    public void setQueryColName(String queryColName) {
        this.queryColName = queryColName;
    }
}
