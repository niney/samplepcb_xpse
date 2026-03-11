package kr.co.samplepcb.xpse.service;

import coolib.common.CCObjectResult;
import coolib.common.CCResult;
import jakarta.persistence.criteria.JoinType;
import kr.co.samplepcb.xpse.domain.entity.SpPartnerOrder;
import kr.co.samplepcb.xpse.pojo.SpPartnerOrderCreateDTO;
import kr.co.samplepcb.xpse.pojo.SpPartnerOrderListDTO;
import kr.co.samplepcb.xpse.pojo.SpPartnerOrderSearchParam;
import kr.co.samplepcb.xpse.pojo.adapter.PagingAdapter;
import kr.co.samplepcb.xpse.repository.SpPartnerOrderRepository;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class SpPartnerOrderService {

    private static final Logger log = LoggerFactory.getLogger(SpPartnerOrderService.class);

    private final SpPartnerOrderRepository spPartnerOrderRepository;

    public SpPartnerOrderService(SpPartnerOrderRepository spPartnerOrderRepository) {
        this.spPartnerOrderRepository = spPartnerOrderRepository;
    }

    @Transactional
    public CCResult create(SpPartnerOrderCreateDTO createDTO) {
        upsert(createDTO);
        return CCObjectResult.setSimpleData(createDTO);
    }

    @Transactional
    public CCResult createBatch(List<SpPartnerOrderCreateDTO> createDTOs) {
        for (SpPartnerOrderCreateDTO dto : createDTOs) {
            upsert(dto);
        }
        return CCObjectResult.setSimpleData(createDTOs);
    }

    @Transactional
    public CCResult search(Pageable pageable, SpPartnerOrderSearchParam searchParam) {
        Specification<SpPartnerOrder> fetchSpec = (root, query, cb) -> {
            if (query.getResultType() != Long.class && query.getResultType() != long.class) {
                root.fetch("shopItem", JoinType.LEFT);
                root.fetch("partner", JoinType.LEFT);
            }
            return cb.conjunction();
        };

        if (StringUtils.isNotBlank(searchParam.getItId())) {
            fetchSpec = fetchSpec.and((root, query, cb) -> cb.equal(root.get("itId"), searchParam.getItId()));
        }
        if (searchParam.getPartnerMbNo() != null) {
            fetchSpec = fetchSpec.and((root, query, cb) -> cb.equal(root.get("partnerMbNo"), searchParam.getPartnerMbNo()));
        }
        if (StringUtils.isNotBlank(searchParam.getStatus())) {
            fetchSpec = fetchSpec.and((root, query, cb) -> cb.equal(root.get("status"), searchParam.getStatus()));
        }

        Page<SpPartnerOrder> page = spPartnerOrderRepository.findAll(fetchSpec, pageable);

        List<SpPartnerOrder> orphans = page.getContent().stream()
                .filter(order -> order.getShopItem() == null)
                .toList();

        if (!orphans.isEmpty()) {
            log.info("삭제된 아이템을 참조하는 고아 주문 {}건 삭제", orphans.size());
            spPartnerOrderRepository.deleteAll(orphans);
            page = spPartnerOrderRepository.findAll(fetchSpec, pageable);
        }

        Page<SpPartnerOrderListDTO> dtoPage = page.map(SpPartnerOrderListDTO::from);
        return PagingAdapter.toCCPagingResult(searchParam.getQ(), pageable, dtoPage);
    }

    private void upsert(SpPartnerOrderCreateDTO dto) {
        Optional<SpPartnerOrder> existing = spPartnerOrderRepository
                .findByItIdAndPartnerMbNo(dto.getItId(), dto.getPartnerMbNo());
        if (existing.isPresent()) {
            SpPartnerOrder entity = existing.get();
            dto.applyTo(entity);
            spPartnerOrderRepository.save(entity);
            return;
        }
        spPartnerOrderRepository.save(dto.toEntity());
    }
}
