package kr.co.samplepcb.xpse.pojo;

public class PcbColumnSearchVM {

    private String id;
    private String colName;
    private Integer target;
    private Double queryScore;
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
