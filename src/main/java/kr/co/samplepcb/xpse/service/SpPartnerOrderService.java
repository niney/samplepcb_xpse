package kr.co.samplepcb.xpse.service;

import coolib.common.CCObjectResult;
import coolib.common.CCResult;
import kr.co.samplepcb.xpse.domain.entity.SpPartnerOrder;
import kr.co.samplepcb.xpse.pojo.SpPartnerOrderCreateDTO;
import kr.co.samplepcb.xpse.pojo.SpPartnerOrderListDTO;
import kr.co.samplepcb.xpse.pojo.SpPartnerOrderSearchParam;
import kr.co.samplepcb.xpse.pojo.adapter.PagingAdapter;
import kr.co.samplepcb.xpse.repository.SpPartnerOrderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class SpPartnerOrderService {

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

    @Transactional(readOnly = true)
    public CCResult search(Pageable pageable, SpPartnerOrderSearchParam searchParam) {
        List<SpPartnerOrderListDTO> list = spPartnerOrderRepository.findPartnerOrderList(pageable, searchParam);
        long total = spPartnerOrderRepository.countPartnerOrderList(searchParam);

        Page<SpPartnerOrderListDTO> dtoPage = new PageImpl<>(list, pageable, total);
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
