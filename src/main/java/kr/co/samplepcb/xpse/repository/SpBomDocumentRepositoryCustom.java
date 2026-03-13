package kr.co.samplepcb.xpse.repository;

import kr.co.samplepcb.xpse.domain.entity.SpBomDocument;
import kr.co.samplepcb.xpse.pojo.SpBomDocumentSearchParam;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SpBomDocumentRepositoryCustom {

    List<SpBomDocument> findBomDocumentList(Pageable pageable, String mbId, SpBomDocumentSearchParam searchParam);

    long countBomDocumentList(String mbId, SpBomDocumentSearchParam searchParam);
}
