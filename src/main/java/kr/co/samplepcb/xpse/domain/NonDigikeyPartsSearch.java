package kr.co.samplepcb.xpse.domain;

import kr.co.samplepcb.xpse.pojo.ElasticIndexName;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

@Document(indexName = ElasticIndexName.NON_DIGIKEY_PARTS)
public class NonDigikeyPartsSearch {

    @Id
    private String id;
    @MultiField(
            mainField = @Field(type = FieldType.Text, analyzer = "ngram_analyzer6_case_insensitive", fielddata = true),
            otherFields = {
                    @InnerField(suffix = "keyword", type = FieldType.Keyword),
                    @InnerField(suffix = "normalize", type = FieldType.Keyword, normalizer = "keyword_normalizer"),
                    @InnerField(suffix = "ngram4", type = FieldType.Text, analyzer = "ngram_analyzer4_case_insensitive")
            }
    )
    private String partName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPartName() {
        return partName;
    }

    public void setPartName(String partName) {
        this.partName = partName;
    }
}
