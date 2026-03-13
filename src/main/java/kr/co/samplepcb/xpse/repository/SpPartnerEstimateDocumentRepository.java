package kr.co.samplepcb.xpse.repository;

import kr.co.samplepcb.xpse.domain.entity.SpPartnerEstimateDocument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpPartnerEstimateDocumentRepository extends JpaRepository<SpPartnerEstimateDocument, Long> {

    Optional<SpPartnerEstimateDocument> findByEstimateDocumentIdAndMbNo(Long estimateDocumentId, int mbNo);
}
