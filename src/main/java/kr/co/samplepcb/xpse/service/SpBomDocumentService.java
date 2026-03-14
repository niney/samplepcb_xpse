package kr.co.samplepcb.xpse.service;

import coolib.common.CCObjectResult;
import coolib.common.CCPagingResult;
import coolib.common.CCResult;
import kr.co.samplepcb.xpse.domain.entity.SpBomDocument;
import kr.co.samplepcb.xpse.mapper.SpBomDocumentMapper;
import kr.co.samplepcb.xpse.pojo.SpBomDocumentDetailDTO;
import kr.co.samplepcb.xpse.pojo.SpBomDocumentCreateDTO;
import kr.co.samplepcb.xpse.pojo.SpBomDocumentListDTO;
import kr.co.samplepcb.xpse.pojo.SpBomDocumentSearchParam;
import kr.co.samplepcb.xpse.pojo.adapter.PagingAdapter;
import kr.co.samplepcb.xpse.repository.SpBomDocumentRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class SpBomDocumentService {

    private final SpBomDocumentRepository spBomDocumentRepository;
    private final SpBomDocumentMapper spBomDocumentMapper;

    public SpBomDocumentService(SpBomDocumentRepository spBomDocumentRepository, SpBomDocumentMapper spBomDocumentMapper) {
        this.spBomDocumentRepository = spBomDocumentRepository;
        this.spBomDocumentMapper = spBomDocumentMapper;
    }

    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    public CCObjectResult<SpBomDocumentDetailDTO> getById(Long id, String mbId) {
        Optional<SpBomDocument> optDoc = spBomDocumentRepository.findById(id);
        if (optDoc.isEmpty() || !mbId.equals(optDoc.get().getMbId())) {
            return dataNotFound();
        }

        return CCObjectResult.setSimpleData(this.spBomDocumentMapper.toDetailDTO(optDoc.get()));
    }

    @SuppressWarnings("unchecked")
    @Transactional
    public CCObjectResult<SpBomDocumentDetailDTO> save(SpBomDocumentCreateDTO dto, String mbId) {
        if (dto == null) {
            return dataNotFound();
        }

        Date now = new Date();
        SpBomDocument target;

        if (dto.getId() != null) {
            Optional<SpBomDocument> optDoc = spBomDocumentRepository.findById(dto.getId());
            if (optDoc.isEmpty() || !mbId.equals(optDoc.get().getMbId())) {
                return dataNotFound();
            }
            target = optDoc.get();
            this.spBomDocumentMapper.updateEntity(target, dto);
            target.setUpdatedAt(now);
            target = this.spBomDocumentRepository.save(target);
            return CCObjectResult.setSimpleData(this.spBomDocumentMapper.toDetailDTO(target));
        }

        String contentHash = dto.getContentHash();
        if (contentHash != null && !contentHash.isBlank()) {
            Optional<SpBomDocument> optDoc = spBomDocumentRepository.findByMbIdAndContentHash(mbId, contentHash);
            if (optDoc.isPresent()) {
                target = optDoc.get();
                if (!mbId.equals(target.getMbId())) {
                    return dataNotFound();
                }
                this.spBomDocumentMapper.updateEntity(target, dto);
                target.setUpdatedAt(now);
                target = this.spBomDocumentRepository.save(target);
                return CCObjectResult.setSimpleData(this.spBomDocumentMapper.toDetailDTO(target));
            }
        }

        target = this.spBomDocumentMapper.toEntity(dto);
        target.setMbId(mbId);
        target.setCreatedAt(now);
        target.setUpdatedAt(now);
        target = this.spBomDocumentRepository.save(target);

        return CCObjectResult.setSimpleData(this.spBomDocumentMapper.toDetailDTO(target));
    }

    @Transactional
    public CCResult delete(Long id, String mbId) {
        Optional<SpBomDocument> optDoc = spBomDocumentRepository.findById(id);
        if (optDoc.isEmpty() || !mbId.equals(optDoc.get().getMbId())) {
            return CCResult.dataNotFound();
        }
        spBomDocumentRepository.delete(optDoc.get());
        return CCResult.ok();
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
