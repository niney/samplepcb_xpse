package kr.co.samplepcb.xpse.repository;

import kr.co.samplepcb.xpse.domain.entity.SpEstimateItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpEstimateItemRepository extends JpaRepository<SpEstimateItem, Long> {

    List<SpEstimateItem> findByEstimateDocumentId(Long estimateDocumentId);

    List<SpEstimateItem> findAllBySelectedPartnerEstimateItemIdIn(List<Long> partnerEstimateItemIds);
}
