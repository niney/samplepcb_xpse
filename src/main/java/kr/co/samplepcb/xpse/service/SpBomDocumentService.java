package kr.co.samplepcb.xpse.service;

import coolib.common.CCObjectResult;
import coolib.common.CCPagingResult;
import coolib.common.CCResult;
import kr.co.samplepcb.xpse.domain.entity.SpBomDocument;
import kr.co.samplepcb.xpse.pojo.SpBomDocumentListDTO;
import kr.co.samplepcb.xpse.pojo.SpBomDocumentSearchParam;
import kr.co.samplepcb.xpse.pojo.adapter.PagingAdapter;
import kr.co.samplepcb.xpse.repository.SpBomDocumentRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class SpBomDocumentService {

    private final SpBomDocumentRepository spBomDocumentRepository;

    public SpBomDocumentService(SpBomDocumentRepository spBomDocumentRepository) {
        this.spBomDocumentRepository = spBomDocumentRepository;
    }

    @Transactional(readOnly = true)
    public CCObjectResult<SpBomDocument> getById(Long id, String mbId) {
        Optional<SpBomDocument> optDoc = spBomDocumentRepository.findById(id);
        if (optDoc.isEmpty() || !mbId.equals(optDoc.get().getMbId())) {
            return dataNotFound();
        }
        return CCObjectResult.setSimpleData(optDoc.get());
    }

    @Transactional(readOnly = true)
    public CCObjectResult<SpBomDocument> getByContentHash(String mbId, String contentHash) {
        Optional<SpBomDocument> optDoc = spBomDocumentRepository.findByMbIdAndContentHash(mbId, contentHash);
        if (optDoc.isEmpty()) {
            return dataNotFound();
        }
        return CCObjectResult.setSimpleData(optDoc.get());
    }

    @Transactional(readOnly = true)
    public CCPagingResult<SpBomDocumentListDTO> search(Pageable pageable, SpBomDocumentSearchParam searchParam, String mbId) {
        if (searchParam == null) {
            searchParam = new SpBomDocumentSearchParam();
        }
        List<SpBomDocument> documents = spBomDocumentRepository.findBomDocumentList(pageable, mbId, searchParam);
        long totalCount = spBomDocumentRepository.countBomDocumentList(mbId, searchParam);
        List<SpBomDocumentListDTO> dtoList = documents.stream().map(doc -> {
            SpBomDocumentListDTO dto = new SpBomDocumentListDTO();
            dto.setId(doc.getId());
            dto.setMbId(doc.getMbId());
            dto.setFileName(doc.getFileName());
            dto.setContentHash(doc.getContentHash());
            dto.setCreatedAt(doc.getCreatedAt());
            dto.setUpdatedAt(doc.getUpdatedAt());
            return dto;
        }).toList();
        return PagingAdapter.toCCPagingResult(searchParam.getQ(), pageable, dtoList, totalCount);
    }

    @SuppressWarnings("unchecked")
    private static <T> CCObjectResult<T> dataNotFound() {
        CCResult result = CCResult.dataNotFound();
        CCObjectResult<T> objectResult = new CCObjectResult<>();
        objectResult.setResult(result.isResult());
        objectResult.setMessage(result.getMessage());
        return objectResult;
    }
}
