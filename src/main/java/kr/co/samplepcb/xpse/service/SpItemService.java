package kr.co.samplepcb.xpse.service;

import coolib.common.CCObjectResult;
import coolib.common.CCResult;
import kr.co.samplepcb.xpse.domain.entity.G5ShopCart;
import kr.co.samplepcb.xpse.domain.entity.G5ShopItem;
import kr.co.samplepcb.xpse.pojo.SpItemCreateDTO;
import kr.co.samplepcb.xpse.pojo.SpItemDetailDTO;
import kr.co.samplepcb.xpse.pojo.SpItemUpdateDTO;
import kr.co.samplepcb.xpse.repository.G5ShopCartRepository;
import kr.co.samplepcb.xpse.repository.G5ShopItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

@Service
public class SpItemService {

    private static final Logger log = LoggerFactory.getLogger(SpItemService.class);

    private final G5ShopItemRepository shopItemRepository;
    private final G5ShopCartRepository shopCartRepository;

    public SpItemService(G5ShopItemRepository shopItemRepository,
                         G5ShopCartRepository shopCartRepository) {
        this.shopItemRepository = shopItemRepository;
        this.shopCartRepository = shopCartRepository;
    }

    /**
     * 상품 생성 + 장바구니 1:1 등록 (PHP itemformupdate_ajax.php + cartupdate_ajax.php 재현).
     * Upsert: 이미 존재하면 UPDATE, 없으면 INSERT.
     */
    @Transactional
    public CCResult create(SpItemCreateDTO createDTO, String mbId, String ipAddress) {
        // itId가 없으면 System.currentTimeMillis()로 자동 생성 (예: 1773309579699)
        if (createDTO.getItId() == null || createDTO.getItId().isBlank()) {
            createDTO.setItId(String.valueOf(System.currentTimeMillis()));
        }

        Optional<G5ShopItem> optItem = shopItemRepository.findById(createDTO.getItId());

        G5ShopItem item;
        if (optItem.isPresent()) {
            // UPDATE (PHP $w == 'u')
            item = optItem.get();
            createDTO.applyTo(item);
        } else {
            // INSERT (PHP $w == '')
            item = createDTO.toG5ShopItem(ipAddress);
        }
        G5ShopItem savedItem = shopItemRepository.save(item);

        // Cart upsert (1:1)
        Optional<G5ShopCart> optCart = shopCartRepository.findByItId(createDTO.getItId());
        if (optCart.isPresent()) {
            G5ShopCart cart = optCart.get();
            createDTO.applyTo(cart);
            shopCartRepository.save(cart);
        } else {
            G5ShopCart cart = createDTO.toG5ShopCart(mbId, ipAddress);
            shopCartRepository.save(cart);
        }

        return CCObjectResult.setSimpleData(SpItemDetailDTO.from(savedItem));
    }

    @Transactional(readOnly = true)
    public CCResult getDetail(String itId) {
        Optional<G5ShopItem> optItem = shopItemRepository.findById(itId);
        if (optItem.isEmpty()) {
            return CCResult.dataNotFound();
        }
        return CCObjectResult.setSimpleData(SpItemDetailDTO.from(optItem.get()));
    }

    @Transactional
    public CCResult update(String itId, SpItemUpdateDTO updateDTO) {
        Optional<G5ShopItem> optItem = shopItemRepository.findById(itId);
        if (optItem.isEmpty()) {
            return CCResult.dataNotFound();
        }
        G5ShopItem item = optItem.get();
        updateDTO.applyTo(item);
        item.setItUpdateTime(new Date());
        G5ShopItem saved = shopItemRepository.save(item);
        return CCObjectResult.setSimpleData(SpItemDetailDTO.from(saved));
    }
}
