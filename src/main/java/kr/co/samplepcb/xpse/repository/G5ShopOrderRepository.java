package kr.co.samplepcb.xpse.repository;

import kr.co.samplepcb.xpse.domain.entity.G5ShopOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface G5ShopOrderRepository extends JpaRepository<G5ShopOrder, Long>, G5ShopOrderRepositoryCustom {

    List<G5ShopOrder> findByMbId(String mbId);
}
