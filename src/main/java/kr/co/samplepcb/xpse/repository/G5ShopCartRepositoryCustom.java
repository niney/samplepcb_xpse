package kr.co.samplepcb.xpse.repository;

import kr.co.samplepcb.xpse.domain.entity.G5ShopCart;
import kr.co.samplepcb.xpse.pojo.SpOrderSearchParam;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface G5ShopCartRepositoryCustom {

    List<G5ShopCart> findOrderList(Pageable pageable, SpOrderSearchParam searchParam);

    long countOrderList(SpOrderSearchParam searchParam);
}
