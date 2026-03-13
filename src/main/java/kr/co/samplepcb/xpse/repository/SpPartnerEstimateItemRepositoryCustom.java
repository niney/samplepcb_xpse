package kr.co.samplepcb.xpse.repository;

import kr.co.samplepcb.xpse.pojo.SpPartnerEstimateItemDetailDTO;
import kr.co.samplepcb.xpse.pojo.SpPartnerEstimateItemListDTO;
import kr.co.samplepcb.xpse.pojo.SpPartnerEstimateItemSearchParam;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SpPartnerEstimateItemRepositoryCustom {

    List<SpPartnerEstimateItemListDTO> findPartnerEstimateItemList(Pageable pageable, SpPartnerEstimateItemSearchParam searchParam);

    long countPartnerEstimateItemList(SpPartnerEstimateItemSearchParam searchParam);

    SpPartnerEstimateItemDetailDTO findPartnerEstimateItemDetail(Long estimateItemId, int mbNo);
}
