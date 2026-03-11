package kr.co.samplepcb.xpse.repository;

import kr.co.samplepcb.xpse.domain.entity.PcbParts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface PcbPartsRepository extends JpaRepository<PcbParts, Long>, JpaSpecificationExecutor<PcbParts> {

    List<PcbParts> findByPartName(String partName);

    List<PcbParts> findByMemberId(String memberId);

    List<PcbParts> findByServiceType(String serviceType);
}
