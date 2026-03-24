package kr.co.samplepcb.xpse.repository;

import kr.co.samplepcb.xpse.domain.entity.SpPartnerOrderDocument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SpPartnerOrderDocumentRepository extends JpaRepository<SpPartnerOrderDocument, Long> {

    Optional<SpPartnerOrderDocument> findByEstimateDocumentIdAndMbNo(Long estimateDocumentId, int mbNo);

    List<SpPartnerOrderDocument> findByEstimateDocumentItId(String itId);
}
