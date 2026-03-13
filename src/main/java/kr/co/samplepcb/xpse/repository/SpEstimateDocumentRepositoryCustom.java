package kr.co.samplepcb.xpse.repository;

import kr.co.samplepcb.xpse.domain.entity.SpEstimateDocument;
import kr.co.samplepcb.xpse.pojo.SpEstimateSearchParam;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SpEstimateDocumentRepositoryCustom {

    List<SpEstimateDocument> findEstimateList(Pageable pageable, SpEstimateSearchParam searchParam);

    long countEstimateList(SpEstimateSearchParam searchParam);
}
