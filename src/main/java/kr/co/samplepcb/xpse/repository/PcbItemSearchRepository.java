package kr.co.samplepcb.xpse.repository;

import kr.co.samplepcb.xpse.domain.PcbItemSearch;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface PcbItemSearchRepository extends ElasticsearchRepository<PcbItemSearch, String> {

    PcbItemSearch findByItemName(String itemName);

    PcbItemSearch findByItemNameAndTarget(String itemName, int target);

    @Query("""
            {
              "bool": {
                "must": [
                  {
                    "match": {
                      "itemName": "?0"
                    }
                  },
                  {
                    "match": {
                      "target": "?1"
                    }
                  }
                ]
              }
            }
            """)
    PcbItemSearch findByItemNameTextAndTarget(String itemName, int target);
}
