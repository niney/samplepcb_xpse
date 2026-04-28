package kr.co.samplepcb.xpse.repository;

import kr.co.samplepcb.xpse.domain.entity.SpPartnerEstimateItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface SpPartnerEstimateItemRepository extends JpaRepository<SpPartnerEstimateItem, Long>, SpPartnerEstimateItemRepositoryCustom {

    List<SpPartnerEstimateItem> findByEstimateItemId(Long estimateItemId);

    Optional<SpPartnerEstimateItem> findByEstimateItemIdAndMbNo(Long estimateItemId, int mbNo);

    Optional<SpPartnerEstimateItem> findByEstimateItemIdAndPartnerEstimateDocumentId(Long estimateItemId, Long partnerEstimateDocumentId);

    long countByPartnerEstimateDocumentId(Long partnerEstimateDocumentId);

    List<SpPartnerEstimateItem> findByPartnerEstimateDocumentId(Long partnerEstimateDocumentId);

    @Query("SELECT pei FROM SpPartnerEstimateItem pei "
            + "JOIN FETCH pei.estimateItem ei "
            + "LEFT JOIN FETCH ei.pcbPart "
            + "WHERE pei.partnerEstimateDocument.id IN :pedIds")
    List<SpPartnerEstimateItem> findByPartnerEstimateDocumentIdInWithPart(@Param("pedIds") Collection<Long> pedIds);
}
