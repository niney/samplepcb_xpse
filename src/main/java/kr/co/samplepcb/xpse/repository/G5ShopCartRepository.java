package kr.co.samplepcb.xpse.repository;

import kr.co.samplepcb.xpse.domain.entity.G5ShopCart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface G5ShopCartRepository extends JpaRepository<G5ShopCart, Integer>, G5ShopCartRepositoryCustom {
}
