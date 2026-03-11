package kr.co.samplepcb.xpse.repository;

import kr.co.samplepcb.xpse.pojo.SpPartnerOrderListDTO;
import kr.co.samplepcb.xpse.pojo.SpPartnerOrderSearchParam;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SpPartnerOrderRepositoryCustom {

    List<SpPartnerOrderListDTO> findPartnerOrderList(Pageable pageable, SpPartnerOrderSearchParam searchParam);

    long countPartnerOrderList(SpPartnerOrderSearchParam searchParam);
}
