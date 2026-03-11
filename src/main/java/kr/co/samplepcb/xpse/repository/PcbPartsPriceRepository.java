package kr.co.samplepcb.xpse.repository;

import kr.co.samplepcb.xpse.domain.entity.PcbPartsPrice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PcbPartsPriceRepository extends JpaRepository<PcbPartsPrice, Long> {

    List<PcbPartsPrice> findByPcbPartsId(Long partsId);
}
