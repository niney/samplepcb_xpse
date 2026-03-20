package kr.co.samplepcb.xpse.repository;

import kr.co.samplepcb.xpse.domain.entity.G5ShopOrder;
import kr.co.samplepcb.xpse.pojo.G5ShopOrderSearchParam;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface G5ShopOrderRepositoryCustom {

    List<G5ShopOrder> findOrderList(Pageable pageable, G5ShopOrderSearchParam searchParam);

    long countOrderList(G5ShopOrderSearchParam searchParam);
}
