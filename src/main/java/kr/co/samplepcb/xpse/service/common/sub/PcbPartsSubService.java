package kr.co.samplepcb.xpse.service.common.sub;

import coolib.common.CCResult;
import kr.co.samplepcb.xpse.domain.document.PcbPartsSearch;
import kr.co.samplepcb.xpse.domain.entity.PcbParts;
import kr.co.samplepcb.xpse.pojo.ElasticIndexName;
import kr.co.samplepcb.xpse.pojo.PcbPartsSearchField;
import kr.co.samplepcb.xpse.repository.PcbPartsRepository;
import kr.co.samplepcb.xpse.repository.PcbPartsSearchRepository;
import kr.co.samplepcb.xpse.util.CoolElasticUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.List;

@Service
public class PcbPartsSubService {

    private static final Logger log = LoggerFactory.getLogger(PcbPartsSubService.class);

    // search
    private final ElasticsearchOperations elasticsearchOperations;

    // repo
    private final PcbPartsSearchRepository pcbPartsSearchRepository;
    private final PcbPartsRepository pcbPartsRepository;

    public PcbPartsSubService(ElasticsearchOperations elasticsearchOperations, PcbPartsSearchRepository pcbPartsSearchRepository, PcbPartsRepository pcbPartsRepository) {
        this.elasticsearchOperations = elasticsearchOperations;
        this.pcbPartsSearchRepository = pcbPartsSearchRepository;
        this.pcbPartsRepository = pcbPartsRepository;
    }

    /**
     * pcbParts 대상 아이템명 모두 변경
     * @param target 대상
     * @param from 이전값
     * @param to 갱신값
     * @return CCResult
     */
    public CCResult updateKindAllByGroup(int target, String from, String to) {
        NativeQuery nativeQuery = NativeQuery.builder()
                .withQuery(q -> q
                        .match(m -> m
                                .field(PcbPartsSearchField.PCB_PART_COLUMN_IDX_TARGET[target] + ".keyword")
                                .query(from)
                        )
                )
                .build();

        SearchHits<PcbPartsSearch> searchHits = this.elasticsearchOperations.search(nativeQuery, PcbPartsSearch.class, IndexCoordinates.of(ElasticIndexName.PCB_PARTS));
        List<PcbPartsSearch> pcbPartsSearches = CoolElasticUtils.unwrapSearchHits(searchHits);
        if(!pcbPartsSearches.isEmpty()) {
            Field field = ReflectionUtils.findField(PcbPartsSearch.class, PcbPartsSearchField.PCB_PART_COLUMN_IDX_TARGET[target]);
            if (field == null) {
                CCResult ccResult = new CCResult();
                ccResult.setResult(false);
                ccResult.setMessage("find not field");
                return ccResult;
            }
            try {
                for (PcbPartsSearch pcbPartsSearch : pcbPartsSearches) {
                    field.setAccessible(true);
                    field.set(pcbPartsSearch, to);
                }
            } catch (IllegalAccessException e) {
                log.error(e.getMessage());
                return CCResult.exceptionSimpleMsg(e);
            }
            this.pcbPartsSearchRepository.saveAll(pcbPartsSearches);

            // DB 동기화: 동일 필드 업데이트
            String dbFieldName = PcbPartsSearchField.PCB_PART_COLUMN_IDX_TARGET[target];
            for (PcbPartsSearch pcbPartsSearch : pcbPartsSearches) {
                if (pcbPartsSearch.getId() != null) {
                    this.pcbPartsRepository.findByDocId(pcbPartsSearch.getId())
                            .ifPresent(entity -> {
                                Field dbField = ReflectionUtils.findField(PcbParts.class, dbFieldName);
                                if (dbField != null) {
                                    try {
                                        dbField.setAccessible(true);
                                        dbField.set(entity, to);
                                        this.pcbPartsRepository.save(entity);
                                    } catch (IllegalAccessException e) {
                                        log.error("Failed to update DB entity field: {}", e.getMessage());
                                    }
                                }
                            });
                }
            }
        }
        return CCResult.ok();
    }

}
