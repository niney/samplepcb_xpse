package kr.co.samplepcb.xpse.repository;

import kr.co.samplepcb.xpse.domain.entity.SpPartnerEstimateItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SpPartnerEstimateItemRepository extends JpaRepository<SpPartnerEstimateItem, Long>, SpPartnerEstimateItemRepositoryCustom {

    List<SpPartnerEstimateItem> findByEstimateItemId(Long estimateItemId);

    Optional<SpPartnerEstimateItem> findByEstimateItemIdAndMbNo(Long estimateItemId, int mbNo);
}
