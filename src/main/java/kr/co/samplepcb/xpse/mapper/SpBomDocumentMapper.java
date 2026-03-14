package kr.co.samplepcb.xpse.mapper;

import kr.co.samplepcb.xpse.domain.entity.SpBomDocument;
import kr.co.samplepcb.xpse.pojo.SpBomDocumentCreateDTO;
import kr.co.samplepcb.xpse.pojo.SpBomDocumentDetailDTO;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

@Mapper(componentModel = "spring")
public interface SpBomDocumentMapper {

    ObjectMapper OBJECT_MAPPER = JsonMapper.builder().build();

    @Mapping(target = "fileInfo", source = "fileInfo", qualifiedByName = "parseJson")
    @Mapping(target = "items", source = "items", qualifiedByName = "parseJson")
    SpBomDocumentDetailDTO toDetailDTO(SpBomDocument doc);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "mbId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "fileInfo", source = "fileInfo", qualifiedByName = "toJsonString")
    @Mapping(target = "items", source = "items", qualifiedByName = "toJsonString")
    SpBomDocument toEntity(SpBomDocumentCreateDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "mbId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "fileInfo", source = "fileInfo", qualifiedByName = "toJsonString")
    @Mapping(target = "items", source = "items", qualifiedByName = "toJsonString")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(@MappingTarget SpBomDocument target, SpBomDocumentCreateDTO dto);

    @Named("parseJson")
    default Object parseJson(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return OBJECT_MAPPER.readValue(value, Object.class);
        } catch (Exception e) {
            return value;
        }
    }

    @Named("toJsonString")
    default String toJsonString(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof String s) {
            if (s.isBlank()) {
                return s;
            }
            String trimmed = s.trim();
            if ((trimmed.startsWith("{") && trimmed.endsWith("}")) || (trimmed.startsWith("[") && trimmed.endsWith("]"))) {
                return trimmed;
            }
            return s;
        }

        try {
            return OBJECT_MAPPER.writeValueAsString(value);
        } catch (Exception e) {
            return String.valueOf(value);
        }
    }
}
