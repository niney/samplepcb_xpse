package kr.co.samplepcb.xpse.repository;

import kr.co.samplepcb.xpse.domain.entity.SpBomDocument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpBomDocumentRepository extends JpaRepository<SpBomDocument, Long>, SpBomDocumentRepositoryCustom {

    Optional<SpBomDocument> findByMbIdAndContentHash(String mbId, String contentHash);
}
