package kr.co.samplepcb.xpse.service;

import coolib.common.CCObjectResult;
import coolib.common.CCResult;
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

import java.util.ArrayList;
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
        SpPartnerOrder saved = upsert(createDTO);
        return CCObjectResult.setSimpleData(saved);
    }

    @Transactional
    public CCResult createBatch(List<SpPartnerOrderCreateDTO> createDTOs) {
        List<SpPartnerOrder> savedList = new ArrayList<>();
        for (SpPartnerOrderCreateDTO dto : createDTOs) {
            savedList.add(upsert(dto));
        }
        return CCObjectResult.setSimpleData(savedList);
    }

    @Transactional(readOnly = true)
    public CCResult search(Pageable pageable, SpPartnerOrderSearchParam searchParam) {
        Specification<SpPartnerOrder> spec = (root, query, cb) -> cb.conjunction();

        if (StringUtils.isNotBlank(searchParam.getItId())) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("itId"), searchParam.getItId()));
        }
        if (searchParam.getPartnerMbNo() != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("partnerMbNo"), searchParam.getPartnerMbNo()));
        }
        if (StringUtils.isNotBlank(searchParam.getStatus())) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), searchParam.getStatus()));
        }

        Page<SpPartnerOrder> page = spPartnerOrderRepository.findAll(spec, pageable);
        Page<SpPartnerOrderListDTO> dtoPage = page.map(SpPartnerOrderListDTO::from);
        return PagingAdapter.toCCPagingResult(searchParam.getQ(), pageable, dtoPage);
    }

    private SpPartnerOrder upsert(SpPartnerOrderCreateDTO dto) {
        Optional<SpPartnerOrder> existing = spPartnerOrderRepository
                .findByItIdAndPartnerMbNo(dto.getItId(), dto.getPartnerMbNo());
        if (existing.isPresent()) {
            SpPartnerOrder entity = existing.get();
            dto.applyTo(entity);
            return spPartnerOrderRepository.save(entity);
        }
        return spPartnerOrderRepository.save(dto.toEntity());
    }
}
