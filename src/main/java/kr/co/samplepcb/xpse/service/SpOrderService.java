package kr.co.samplepcb.xpse.service;

import coolib.common.CCResult;
import kr.co.samplepcb.xpse.domain.entity.G5ShopCart;
import kr.co.samplepcb.xpse.pojo.SpOrderListDTO;
import kr.co.samplepcb.xpse.pojo.SpOrderSearchParam;
import kr.co.samplepcb.xpse.pojo.adapter.PagingAdapter;
import kr.co.samplepcb.xpse.repository.G5ShopCartRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SpOrderService {

    private static final Logger log = LoggerFactory.getLogger(SpOrderService.class);

    private final G5ShopCartRepository shopCartRepository;

    public SpOrderService(G5ShopCartRepository shopCartRepository) {
        this.shopCartRepository = shopCartRepository;
    }

    @Transactional(readOnly = true)
    public CCResult search(Pageable pageable, SpOrderSearchParam searchParam) {
        List<G5ShopCart> carts = shopCartRepository.findOrderList(pageable, searchParam);
        long totalCount = shopCartRepository.countOrderList(searchParam);
        List<SpOrderListDTO> dtoList = carts.stream()
                .map(SpOrderListDTO::from)
                .toList();
        return PagingAdapter.toCCPagingResult(searchParam.getQ(), pageable, dtoList, totalCount);
    }
}
