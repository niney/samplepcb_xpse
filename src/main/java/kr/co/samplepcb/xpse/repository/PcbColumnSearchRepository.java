package kr.co.samplepcb.xpse.repository;

import kr.co.samplepcb.xpse.domain.PcbColumnSearch;
import kr.co.samplepcb.xpse.pojo.PcbColumnSearchField;
import kr.co.samplepcb.xpse.pojo.PcbColumnSearchVM;
import kr.co.samplepcb.xpse.util.CoolElasticUtils;
import org.springframework.data.elasticsearch.core.query.*;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.HashSet;
import java.util.Set;

public interface PcbColumnSearchRepository extends ElasticsearchRepository<PcbColumnSearch, String> {

    default Query createSearchQuery(PcbColumnSearchVM pcbColumnSearchVM) {
        Criteria criteria = new Criteria();
        Set<String> highlightSet = new HashSet<>();
        if (pcbColumnSearchVM.getColName() != null) {
            criteria = criteria.and(PcbColumnSearchField.COL_NAME).is(pcbColumnSearchVM.getColName());
            highlightSet.add(PcbColumnSearchField.COL_NAME);
        }
        if (pcbColumnSearchVM.getTarget() != null) {
            criteria = criteria.and(PcbColumnSearchField.TARGET).is(pcbColumnSearchVM.getTarget());
            highlightSet.add(PcbColumnSearchField.TARGET);
        }
        HighlightQuery highlightQuery = CoolElasticUtils.createHighlightQuery(highlightSet);

        Query query = new CriteriaQuery(criteria);
        query.setHighlightQuery(highlightQuery);
        return query;
    }

}
