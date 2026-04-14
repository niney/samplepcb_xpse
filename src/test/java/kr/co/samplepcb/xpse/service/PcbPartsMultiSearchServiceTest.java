package kr.co.samplepcb.xpse.service;

import coolib.common.CCObjectResult;
import coolib.common.CCResult;
import kr.co.samplepcb.xpse.config.ApplicationProperties;
import kr.co.samplepcb.xpse.domain.document.PcbPartsSearch;
import kr.co.samplepcb.xpse.pojo.PcbPartsMultiSearchResult;
import kr.co.samplepcb.xpse.repository.PcbPartsSearchRepository;
import kr.co.samplepcb.xpse.service.common.sub.DigikeyPartsParserSubService;
import kr.co.samplepcb.xpse.service.common.sub.DigikeySubService;
import kr.co.samplepcb.xpse.service.common.sub.UniKeyICPartsParserSubService;
import kr.co.samplepcb.xpse.service.common.sub.UniKeyICSubService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Query;

import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PcbPartsMultiSearchServiceTest {

    @Mock
    private ElasticsearchOperations elasticsearchOperations;
    @Mock
    private DigikeySubService digikeySubService;
    @Mock
    private DigikeyPartsParserSubService digikeyPartsParserSubService;
    @Mock
    private UniKeyICSubService uniKeyICSubService;
    @Mock
    private UniKeyICPartsParserSubService uniKeyICPartsParserSubService;
    @Mock
    private PcbPartsService pcbPartsService;
    @Mock
    private PcbPartsSearchRepository pcbPartsSearchRepository;
    @Mock
    private ApplicationProperties applicationProperties;

    @InjectMocks
    private PcbPartsMultiSearchService service;

    /**
     * ES에 24시간 이내 캐시된 데이터가 있으면 외부 API를 호출하지 않는지 검증
     */
    @Test
    @SuppressWarnings("unchecked")
    void searchMultiSource_esCacheHit_skipsExternalApis() {
        // given: ES에 1시간 전 색인된 digikey/unikeyic 데이터 존재
        PcbPartsSearch cachedDigikey = new PcbPartsSearch();
        cachedDigikey.setPartName("LM358");
        cachedDigikey.setServiceType("digikey");
        cachedDigikey.setLastModifiedDate(new Date(System.currentTimeMillis() - 3_600_000L));

        PcbPartsSearch cachedUnikeyic = new PcbPartsSearch();
        cachedUnikeyic.setPartName("LM358");
        cachedUnikeyic.setServiceType("unikeyic");
        cachedUnikeyic.setLastModifiedDate(new Date(System.currentTimeMillis() - 3_600_000L));

        ApplicationProperties.ExternalCache externalCache = new ApplicationProperties.ExternalCache();
        when(applicationProperties.getExternalCache()).thenReturn(externalCache);

        when(pcbPartsSearchRepository.findByServiceTypeAndPartNameKeywordIn("digikey", List.of("LM358")))
                .thenReturn(List.of(cachedDigikey));
        when(pcbPartsSearchRepository.findByServiceTypeAndPartNameKeywordIn("unikeyic", List.of("LM358")))
                .thenReturn(List.of(cachedUnikeyic));

        // samplepcb ES 검색 — 빈 결과
        SearchHits<PcbPartsSearch> emptyHits = mock(SearchHits.class);
        when(emptyHits.hasSearchHits()).thenReturn(false);
        when(elasticsearchOperations.search(any(Query.class), eq(PcbPartsSearch.class))).thenReturn(emptyHits);

        // when
        CCResult result = service.searchMultiSource("LM358", null).block();

        // then
        assertThat(result).isNotNull();
        assertThat(result.isResult()).isTrue();

        PcbPartsMultiSearchResult data = (PcbPartsMultiSearchResult)
                ((CCObjectResult<?>) result).getData();

        assertThat(data.getDigikey().getSearchType()).isEqualTo("exact");
        assertThat(data.getDigikey().getItems()).hasSize(1);
        assertThat(data.getUnikeyic().getSearchType()).isEqualTo("exact");
        assertThat(data.getUnikeyic().getItems()).hasSize(1);

        // 외부 API 호출 없음 검증
        verifyNoInteractions(digikeySubService);
        verifyNoInteractions(uniKeyICSubService);
    }
}
