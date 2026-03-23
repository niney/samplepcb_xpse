package kr.co.samplepcb.xpse.repository;

import kr.co.samplepcb.xpse.domain.entity.SpPartnerOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpPartnerOrderItemRepository extends JpaRepository<SpPartnerOrderItem, Long> {

    Optional<SpPartnerOrderItem> findByEstimateItemIdAndPartnerOrderDocumentId(Long estimateItemId, Long partnerOrderDocumentId);
}
