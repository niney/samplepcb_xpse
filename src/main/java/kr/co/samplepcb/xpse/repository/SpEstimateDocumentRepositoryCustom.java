package kr.co.samplepcb.xpse.repository;

import kr.co.samplepcb.xpse.pojo.SpEstimateListDTO;
import kr.co.samplepcb.xpse.pojo.SpEstimateSearchParam;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SpEstimateDocumentRepositoryCustom {

    List<SpEstimateListDTO> findEstimateList(Pageable pageable, SpEstimateSearchParam searchParam);

    long countEstimateList(SpEstimateSearchParam searchParam);
}
