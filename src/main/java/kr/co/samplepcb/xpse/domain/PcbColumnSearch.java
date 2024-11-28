package kr.co.samplepcb.xpse.domain;

import kr.co.samplepcb.xpse.pojo.ElasticIndexName;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

import java.util.List;

@Document(indexName = ElasticIndexName.PCB_COLUMN)
public class PcbColumnSearch extends SearchBase {

    @Id
    private String id;

    @MultiField(
            mainField = @Field(type = FieldType.Text, analyzer = "ngram_analyzer_case_insensitive", fielddata = true),
            otherFields = {
                    @InnerField(suffix = "keyword", type = FieldType.Keyword)
            }
    )
    private String colName;

    @Field(type = FieldType.Keyword)
    private Integer target;

    @Field(type = FieldType.Dense_Vector, dims = 512)
    private List<Double> colNameVector;

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

    public List<Double> getColNameVector() {
        return colNameVector;
    }

    public void setColNameVector(List<Double> colNameVector) {
        this.colNameVector = colNameVector;
    }

    @Override
    public String toString() {
        return "PcbColumnSearch{" +
                "id='" + id + '\'' +
                ", colName='" + colName + '\'' +
                ", target=" + target +
                ", colNameVector=" + colNameVector +
                '}';
    }
}
