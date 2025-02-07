package kr.co.samplepcb.xpse.domain;

import kr.co.samplepcb.xpse.pojo.ElasticIndexName;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

@Document(indexName = ElasticIndexName.PCB_ITEM)
public class PcbItemSearch {

    @Id
    private String id;

    @MultiField(
            mainField = @Field(type = FieldType.Keyword, normalizer = "lowercase"),
            otherFields = {
                    @InnerField(suffix = "text", type = FieldType.Text,
                            analyzer = "ngram_analyzer_case_insensitive", fielddata = true)
            }
    )
    private String itemName;

    @Field(type = FieldType.Keyword)
    private Integer target;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Integer getTarget() {
        return target;
    }

    public void setTarget(Integer target) {
        this.target = target;
    }
}
