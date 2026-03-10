package kr.co.samplepcb.xpse.service;

import coolib.common.CCObjectResult;
import coolib.common.CCResult;
import kr.co.samplepcb.xpse.domain.entity.G5ShopItem;
import kr.co.samplepcb.xpse.pojo.SpItemDetailDTO;
import kr.co.samplepcb.xpse.repository.G5ShopItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class SpItemService {

    private static final Logger log = LoggerFactory.getLogger(SpItemService.class);

    private final G5ShopItemRepository shopItemRepository;

    public SpItemService(G5ShopItemRepository shopItemRepository) {
        this.shopItemRepository = shopItemRepository;
    }

    @Transactional(readOnly = true)
    public CCResult getDetail(String itId) {
        Optional<G5ShopItem> optItem = shopItemRepository.findById(itId);
        if (optItem.isEmpty()) {
            return CCResult.dataNotFound();
        }
        return CCObjectResult.setSimpleData(SpItemDetailDTO.from(optItem.get()));
    }
}
