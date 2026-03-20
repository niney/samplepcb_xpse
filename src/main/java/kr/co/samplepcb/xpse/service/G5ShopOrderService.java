package kr.co.samplepcb.xpse.service;

import coolib.common.CCPagingResult;
import kr.co.samplepcb.xpse.domain.entity.G5ShopOrder;
import kr.co.samplepcb.xpse.pojo.G5ShopOrderListDTO;
import kr.co.samplepcb.xpse.pojo.G5ShopOrderSearchParam;
import kr.co.samplepcb.xpse.pojo.adapter.PagingAdapter;
import kr.co.samplepcb.xpse.repository.G5ShopOrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class G5ShopOrderService {

    private static final Logger log = LoggerFactory.getLogger(G5ShopOrderService.class);

    private final G5ShopOrderRepository shopOrderRepository;

    public G5ShopOrderService(G5ShopOrderRepository shopOrderRepository) {
        this.shopOrderRepository = shopOrderRepository;
    }

    @Transactional(readOnly = true)
    public CCPagingResult<G5ShopOrderListDTO> search(Pageable pageable, G5ShopOrderSearchParam searchParam) {
        List<G5ShopOrder> orders = shopOrderRepository.findOrderList(pageable, searchParam);
        long totalCount = shopOrderRepository.countOrderList(searchParam);
        List<G5ShopOrderListDTO> dtoList = orders.stream()
                .map(G5ShopOrderListDTO::from)
                .toList();
        return PagingAdapter.toCCPagingResult(searchParam.getQ(), pageable, dtoList, totalCount);
    }
}
