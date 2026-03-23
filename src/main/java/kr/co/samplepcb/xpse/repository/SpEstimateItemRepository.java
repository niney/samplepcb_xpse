package kr.co.samplepcb.xpse.repository;

import kr.co.samplepcb.xpse.domain.entity.SpEstimateItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SpEstimateItemRepository extends JpaRepository<SpEstimateItem, Long> {

    List<SpEstimateItem> findByEstimateDocumentId(Long estimateDocumentId);

    List<SpEstimateItem> findAllBySelectedPartnerEstimateItemIdIn(List<Long> partnerEstimateItemIds);

    @Query("SELECT ei FROM SpEstimateItem ei JOIN FETCH ei.estimateDocument WHERE ei.id IN :ids")
    List<SpEstimateItem> findAllByIdWithDocument(@Param("ids") List<Long> ids);
}
