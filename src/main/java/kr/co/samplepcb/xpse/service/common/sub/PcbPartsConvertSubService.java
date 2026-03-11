package kr.co.samplepcb.xpse.service.common.sub;

import kr.co.samplepcb.xpse.domain.document.PcbPartsPriceSearch;
import kr.co.samplepcb.xpse.domain.document.PcbPartsPriceStepSearch;
import kr.co.samplepcb.xpse.domain.document.PcbPartsSearch;
import kr.co.samplepcb.xpse.domain.document.PcbUnitSearch;
import kr.co.samplepcb.xpse.domain.entity.*;
import kr.co.samplepcb.xpse.pojo.PcbImageVM;
import kr.co.samplepcb.xpse.pojo.PcbPartSpec;
import kr.co.samplepcb.xpse.util.DocIdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class PcbPartsConvertSubService {

    private static final Logger log = LoggerFactory.getLogger(PcbPartsConvertSubService.class);
    private static final ObjectMapper objectMapper = JsonMapper.builder().build();

    /**
     * PcbPartsSearch (ES document) → PcbParts (JPA entity) 변환
     * docId가 없으면 자동 생성
     */
    public PcbParts toEntity(PcbPartsSearch search) {
        PcbParts entity = new PcbParts();
        String docId = search.getId() != null ? search.getId() : DocIdGenerator.generate();
        entity.setDocId(docId);

        Date now = new Date();
        entity.setWriteDate(search.getWriteDate() != null ? search.getWriteDate() : now);
        entity.setLastModifiedDate(search.getLastModifiedDate() != null ? search.getLastModifiedDate() : now);

        entity.setServiceType(search.getServiceType());
        entity.setSubServiceType(search.getSubServiceType());
        entity.setLargeCategory(search.getLargeCategory());
        entity.setMediumCategory(search.getMediumCategory());
        entity.setSmallCategory(search.getSmallCategory());
        entity.setPartName(search.getPartName());
        entity.setDescription(search.getDescription());
        entity.setManufacturerName(search.getManufacturerName());
        entity.setPartsPackaging(search.getPartsPackaging());
        entity.setPackaging(toJson(search.getPackaging()));
        entity.setMoq(search.getMoq());
        entity.setPrice(search.getPrice());
        entity.setMemo(search.getMemo());
        entity.setOfferName(search.getOfferName());
        entity.setDateCode(search.getDateCode());
        entity.setMemberId(search.getMemberId());
        entity.setManagerPhoneNumber(search.getManagerPhoneNumber());
        entity.setManagerName(search.getManagerName());
        entity.setManagerEmail(search.getManagerEmail());
        entity.setContents(search.getContents());
        entity.setStatus(search.getStatus());
        entity.setWatt(toJson(search.getWatt()));
        entity.setTolerance(toJson(search.getTolerance()));
        entity.setOhm(toJson(search.getOhm()));
        entity.setCondenser(toJson(search.getCondenser()));
        entity.setVoltage(toJson(search.getVoltage()));
        entity.setTemperature(search.getTemperature());
        entity.setSize(search.getSize());
        entity.setCurrentVal(toJson(search.getCurrent()));
        entity.setInductor(toJson(search.getInductor()));
        entity.setProductName(search.getProductName());
        entity.setPhotoUrl(search.getPhotoUrl());
        entity.setDatasheetUrl(search.getDatasheetUrl());

        // prices
        if (search.getPrices() != null) {
            for (PcbPartsPriceSearch ps : search.getPrices()) {
                PcbPartsPrice priceEntity = new PcbPartsPrice();
                priceEntity.setPcbParts(entity);
                priceEntity.setDistributor(ps.getDistributor());
                priceEntity.setSku(ps.getSku());
                priceEntity.setStock(ps.getStock());
                priceEntity.setMoq(ps.getMoq());
                priceEntity.setPkg(ps.getPkg());
                priceEntity.setUpdatedDate(ps.getUpdatedDate());
                if (ps.getPriceSteps() != null) {
                    for (PcbPartsPriceStepSearch ss : ps.getPriceSteps()) {
                        PcbPartsPriceStep stepEntity = new PcbPartsPriceStep();
                        stepEntity.setPcbPartsPrice(priceEntity);
                        stepEntity.setBreakQuantity(ss.getBreakQuantity());
                        stepEntity.setUnitPrice(ss.getUnitPrice());
                        priceEntity.getPriceSteps().add(stepEntity);
                    }
                }
                entity.getPrices().add(priceEntity);
            }
        }

        // images
        if (search.getImages() != null) {
            for (PcbImageVM img : search.getImages()) {
                PcbPartsImage imageEntity = new PcbPartsImage();
                imageEntity.setPcbParts(entity);
                imageEntity.setUploadFileName(img.getUploadFileName());
                imageEntity.setOriginFileName(img.getOriginFileName());
                imageEntity.setPathToken(img.getPathToken());
                imageEntity.setSize(img.getSize());
                entity.getImages().add(imageEntity);
            }
        }

        // specs
        if (search.getSpecs() != null) {
            for (PcbPartSpec spec : search.getSpecs()) {
                PcbPartsSpec specEntity = new PcbPartsSpec();
                specEntity.setPcbParts(entity);
                specEntity.setDisplayValue(spec.getDisplayValue());
                if (spec.getAttribute() != null) {
                    specEntity.setAttrGroup(spec.getAttribute().getGroup());
                    specEntity.setAttrName(spec.getAttribute().getName());
                    specEntity.setAttrShortname(spec.getAttribute().getShortname());
                }
                entity.getSpecs().add(specEntity);
            }
        }

        return entity;
    }

    /**
     * PcbPartsSearch 리스트 → PcbParts 엔티티 리스트 변환
     * 각 항목에 docId를 생성하고 PcbPartsSearch.id에도 세팅
     */
    public List<PcbParts> toEntities(List<PcbPartsSearch> searchList) {
        List<PcbParts> entities = new ArrayList<>();
        for (PcbPartsSearch search : searchList) {
            if (search.getId() == null) {
                search.setId(DocIdGenerator.generate());
            }
            PcbParts entity = toEntity(search);
            entities.add(entity);
        }
        return entities;
    }

    private String toJson(Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            log.warn("Failed to serialize object to JSON: {}", e.getMessage());
            return null;
        }
    }
}
