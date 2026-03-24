package kr.co.samplepcb.xpse.repository;

import kr.co.samplepcb.xpse.domain.entity.SpEstimateDocument;
import kr.co.samplepcb.xpse.pojo.SpEstimateListDTO;
import kr.co.samplepcb.xpse.pojo.SpEstimateSearchParam;
import kr.co.samplepcb.xpse.pojo.SpPartnerEstimateDocDetailDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface SpEstimateDocumentRepositoryCustom {

    List<SpEstimateListDTO> findEstimateList(Pageable pageable, SpEstimateSearchParam searchParam);

    long countEstimateList(SpEstimateSearchParam searchParam);

    Optional<SpEstimateDocument> findDetailById(Long id);

    Optional<SpEstimateDocument> findDetailByItId(String itId);

    List<SpEstimateListDTO> findEstimateListForPartner(Pageable pageable, SpEstimateSearchParam searchParam, int mbNo);

    long countEstimateListForPartner(SpEstimateSearchParam searchParam, int mbNo);

    List<SpPartnerEstimateDocDetailDTO.ItemDTO> findDetailItemsForPartner(Long docId, Long pedId);

    List<SpEstimateListDTO> findEstimateListWithPartners(Pageable pageable, SpEstimateSearchParam searchParam);

    List<SpEstimateListDTO> findEstimateListWithPartnerOrders(Pageable pageable, SpEstimateSearchParam searchParam);

    List<SpEstimateListDTO> findEstimateListWithOrder(Pageable pageable, SpEstimateSearchParam searchParam);

    long countEstimateListWithOrder(SpEstimateSearchParam searchParam);
}
