package kr.co.samplepcb.xpse.repository;

import kr.co.samplepcb.xpse.domain.entity.SpEstimateDocument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpEstimateDocumentRepository extends JpaRepository<SpEstimateDocument, Long>, SpEstimateDocumentRepositoryCustom {

    Optional<SpEstimateDocument> findByItId(String itId);
}
