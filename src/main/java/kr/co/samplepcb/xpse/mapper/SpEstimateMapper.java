package kr.co.samplepcb.xpse.mapper;

import kr.co.samplepcb.xpse.domain.entity.PcbParts;
import kr.co.samplepcb.xpse.domain.entity.SpEstimateDocument;
import kr.co.samplepcb.xpse.domain.entity.SpEstimateItem;
import kr.co.samplepcb.xpse.domain.entity.SpFile;
import kr.co.samplepcb.xpse.pojo.SpEstimateCreateDTO;
import kr.co.samplepcb.xpse.pojo.SpEstimateDetailDTO;
import org.mapstruct.*;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SpEstimateMapper {

    ObjectMapper OBJECT_MAPPER = JsonMapper.builder().build();

    // ── Entity → Detail DTO ──

    @Mapping(target = "files", ignore = true)
    @Mapping(target = "itName", source = "shopItem.itName")
    @Mapping(target = "mbName", source = "shopCart.member.mbName")
    @Mapping(target = "mbEmail", source = "shopCart.member.mbEmail")
    @Mapping(target = "mbTel", source = "shopCart.member.mbTel")
    @Mapping(target = "mbHp", source = "shopCart.member.mbHp")
    SpEstimateDetailDTO toDetailDTO(SpEstimateDocument doc);

    @Mapping(target = "analysisMeta", source = "analysisMeta", qualifiedByName = "parseJson")
    @Mapping(target = "selectedPrice", source = "selectedPrice", qualifiedByName = "parseJson")
    @Mapping(target = "selectedPartnerEstimateItemId", source = "selectedPartnerEstimateItem.id")
    SpEstimateDetailDTO.EstimateItemDTO toEstimateItemDTO(SpEstimateItem item);

    SpEstimateDetailDTO.PcbPartDTO toPcbPartDTO(PcbParts pcbParts);

    SpEstimateDetailDTO.FileDTO toFileDTO(SpFile file);

    List<SpEstimateDetailDTO.FileDTO> toFileDTOs(List<SpFile> files);

    default SpEstimateDetailDTO toDetailDTO(SpEstimateDocument doc, List<SpFile> files) {
        SpEstimateDetailDTO dto = toDetailDTO(doc);
        dto.setFiles(toFileDTOs(files));
        return dto;
    }

    // ── DTO → Entity (create/update) ──

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "itId", ignore = true)
    @Mapping(target = "shopItem", ignore = true)
    @Mapping(target = "shopCart", ignore = true)
    @Mapping(target = "items", ignore = true)
    @Mapping(target = "writeDate", ignore = true)
    @Mapping(target = "modifyDate", ignore = true)
    void updateDocument(SpEstimateCreateDTO dto, @MappingTarget SpEstimateDocument doc);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "estimateDocument", ignore = true)
    @Mapping(target = "pcbPart", ignore = true)
    @Mapping(target = "selectedPartnerEstimateItem", ignore = true)
    @Mapping(target = "partnerEstimateItems", ignore = true)
    @Mapping(target = "writeDate", ignore = true)
    @Mapping(target = "modifyDate", ignore = true)
    SpEstimateItem toEstimateItemEntity(SpEstimateCreateDTO.EstimateItemDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "refType", ignore = true)
    @Mapping(target = "refId", ignore = true)
    @Mapping(target = "writeDate", ignore = true)
    SpFile toFileEntity(SpEstimateCreateDTO.FileDTO dto);

    // ── Custom mapping ──

    @Named("parseJson")
    default Object parseJson(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return OBJECT_MAPPER.readValue(value, Object.class);
        } catch (JacksonException e) {
            return value;
        }
    }
}
