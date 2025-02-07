package kr.co.samplepcb.xpse.repository;

import kr.co.samplepcb.xpse.domain.NonDigikeyPartsSearch;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface NonDigikeyPartsSearchRepository extends ElasticsearchRepository<NonDigikeyPartsSearch, String> {

    @Query("""
            {
              "match": {
                "partName.keyword": {
                  "query": "?0"
                }
              }
            }
            """)
    List<NonDigikeyPartsSearch> findByPartNameKeyword(String partName);
}
