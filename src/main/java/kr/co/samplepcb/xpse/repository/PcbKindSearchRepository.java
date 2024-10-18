package kr.co.samplepcb.xpse.repository;

import coolib.common.QueryParam;
import kr.co.samplepcb.xpse.domain.PcbKindSearch;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface PcbKindSearchRepository extends ElasticsearchRepository<PcbKindSearch, String> {

    PcbKindSearch findByItemNameAndTarget(String itemName, int target);

    @Query("{\n" +
            "    \"bool\": {\n" +
            "      \"must\": [\n" +
            "        {\n" +
            "          \"match\": {\n" +
            "            \"itemName.normalize\": \"?0\"\n" +
            "          }\n" +
            "        },\n" +
            "        {\n" +
            "          \"match\": {\n" +
            "            \"target\": ?1\n" +
            "          }\n" +
            "        }\n" +
            "      ]\n" +
            "    }\n" +
            "  }")
    PcbKindSearch findByItemNameKeywordAndTarget(String itemName, int target);

    @Query("{\n" +
            "    \"bool\": {\n" +
            "      \"must\": [\n" +
            "        {\n" +
            "          \"match\": {\n" +
            "            \"displayName.normalize\": \"?0\"\n" +
            "          }\n" +
            "        },\n" +
            "        {\n" +
            "          \"match\": {\n" +
            "            \"target\": ?1\n" +
            "          }\n" +
            "        }\n" +
            "      ]\n" +
            "    }\n" +
            "  }")
    PcbKindSearch findByDisplayNameKeywordAndTarget(String itemName, int target);


    List<PcbKindSearch> findAllByTarget(int target);

    void deleteByTarget(int target);

}
