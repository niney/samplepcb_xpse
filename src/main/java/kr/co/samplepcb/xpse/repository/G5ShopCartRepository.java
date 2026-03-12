package kr.co.samplepcb.xpse.repository;

import kr.co.samplepcb.xpse.domain.entity.G5ShopCart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface G5ShopCartRepository extends JpaRepository<G5ShopCart, Integer>, G5ShopCartRepositoryCustom {

    Optional<G5ShopCart> findByItId(String itId);
}
