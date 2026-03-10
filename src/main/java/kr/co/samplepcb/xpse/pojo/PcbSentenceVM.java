package kr.co.samplepcb.xpse.pojo;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "PCB 문장 검색 조건")
public class PcbSentenceVM {

    @Schema(description = "검색 컬럼명 목록")
    private List<String> queryColumnNameList;
    @Schema(description = "PCB 컬럼 검색 목록")
    private List<PcbColumnSearchVM> pcbColumnSearchList;
    @Schema(description = "평균 점수")
    private Double averageScore;

    public List<String> getQueryColumnNameList() {
        return queryColumnNameList;
    }

    public void setQueryColumnNameList(List<String> queryColumnNameList) {
        this.queryColumnNameList = queryColumnNameList;
    }

    public List<PcbColumnSearchVM> getPcbColumnSearchList() {
        return pcbColumnSearchList;
    }

    public void setPcbColumnSearchList(List<PcbColumnSearchVM> pcbColumnSearchList) {
        this.pcbColumnSearchList = pcbColumnSearchList;
    }

    public Double getAverageScore() {
        return averageScore;
    }

    public void setAverageScore(Double averageScore) {
        this.averageScore = averageScore;
    }
}
