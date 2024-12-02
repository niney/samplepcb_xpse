package kr.co.samplepcb.xpse.repository;

import coolib.common.QueryParam;
import kr.co.samplepcb.xpse.domain.PcbPartsSearch;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.lang.reflect.Field;
import java.util.List;

public interface PcbPartsSearchRepository extends ElasticsearchRepository<PcbPartsSearch, String> {

    Logger log = LoggerFactory.getLogger(PcbPartsSearchRepository.class);

    @Query("{\n" +
            "    \"bool\": {\n" +
            "      \"must\": [\n" +
            "        {\n" +
            "          \"match\": {\n" +
            "            \"partName.normalize\": \"?0\"\n" +
            "          }\n" +
            "        },\n" +
            "        {\n" +
            "          \"match\": {\n" +
            "            \"memberId\": \"?1\"\n" +
            "          }\n" +
            "        }\n" +
            "      ]\n" +
            "    }\n" +
            "  }")
    PcbPartsSearch findByPartNameNormalizeAndMemberId(String partName, String memberId);

    @Query("""
            {
              "match": {
                "partName.keyword": "?0"
              }
            }
            """)
    PcbPartsSearch findByPartNameKeyword(String partName);


}
