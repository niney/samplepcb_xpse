package kr.co.samplepcb.xpse.repository;

import kr.co.samplepcb.xpse.domain.entity.SpPartnerOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SpPartnerOrderRepository extends JpaRepository<SpPartnerOrder, Long> {

    List<SpPartnerOrder> findByItIdIn(List<String> itIds);

    Optional<SpPartnerOrder> findByItIdAndPartnerMbNo(String itId, int partnerMbNo);
}
