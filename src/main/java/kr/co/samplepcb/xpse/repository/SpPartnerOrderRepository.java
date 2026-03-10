package kr.co.samplepcb.xpse.repository;

import kr.co.samplepcb.xpse.domain.entity.SpPartnerOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpPartnerOrderRepository extends JpaRepository<SpPartnerOrder, Long> {

    List<SpPartnerOrder> findByItIdIn(List<String> itIds);
}
