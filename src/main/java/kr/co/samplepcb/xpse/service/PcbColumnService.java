package kr.co.samplepcb.xpse.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.Script;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchAllQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.ScriptScoreQuery;
import co.elastic.clients.elasticsearch.core.MsearchRequest;
import co.elastic.clients.elasticsearch.core.MsearchResponse;
import co.elastic.clients.elasticsearch.core.msearch.*;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.JsonData;
import coolib.common.CCObjectResult;
import coolib.common.CCResult;
import coolib.common.QueryParam;
import kr.co.samplepcb.xpse.domain.PcbColumnSearch;
import kr.co.samplepcb.xpse.pojo.ElasticIndexName;
import kr.co.samplepcb.xpse.pojo.PcbColumnSearchField;
import kr.co.samplepcb.xpse.pojo.PcbColumnSearchVM;
import kr.co.samplepcb.xpse.pojo.PcbSentenceVM;
import kr.co.samplepcb.xpse.pojo.adapter.PagingAdapter;
import kr.co.samplepcb.xpse.repository.PcbColumnSearchRepository;
import kr.co.samplepcb.xpse.service.common.sub.GoogleTensorService;
import kr.co.samplepcb.xpse.util.CoolElasticUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PcbColumnService {

    private static final Logger log = LoggerFactory.getLogger(PcbColumnService.class);

    // search
    private final ElasticsearchClient esClient;
    // service
    private final GoogleTensorService googleTensorService;
    // repo
    private final ElasticsearchOperations elasticsearchOperations;
    private final PcbColumnSearchRepository pcbColumnSearchRepository;

    public PcbColumnService(ElasticsearchClient esClient, GoogleTensorService googleTensorService, ElasticsearchOperations elasticsearchOperations, PcbColumnSearchRepository pcbColumnSearchRepository) {
        this.esClient = esClient;
        this.googleTensorService = googleTensorService;
        this.elasticsearchOperations = elasticsearchOperations;
        this.pcbColumnSearchRepository = pcbColumnSearchRepository;
    }

    public CCResult search(Pageable pageable, QueryParam queryParam, PcbColumnSearchVM pcbColumnSearchVM) {
        Query query = this.pcbColumnSearchRepository.createSearchQuery(pcbColumnSearchVM);
        query.setPageable(pageable);
        query.addSourceFilter(new FetchSourceFilter(new String[]{
                PcbColumnSearchField.TARGET,
                PcbColumnSearchField.COL_NAME
        }, null));
        SearchHits<PcbColumnSearch> searchHits = this.elasticsearchOperations.search(query, PcbColumnSearch.class);
        return PagingAdapter.toCCPagingResult(pageable, CoolElasticUtils.getSourceWithHighlight(searchHits), searchHits.getTotalHits());
    }

    public CCResult searchSentenceList(Pageable pageable, PcbSentenceVM pcbSentenceVM) {
        List<String> columnNameList = pcbSentenceVM.getQueryColumnNameList();
        Mono<CCObjectResult<List<List<Double>>>> vectorsMono = this.googleTensorService.postEncodedSentences(columnNameList);
        CCObjectResult<List<List<Double>>> vectorsResult = vectorsMono.block();
        if (vectorsResult == null || vectorsResult.getData().isEmpty()) {
            log.warn("백터 변환 실패");
            return new CCResult.Builder()
                    .setFailMessage("백터 변환 실패")
                    .build();
        }

        List<PcbColumnSearchVM> pcbColumnSearchVMList = this.searchSentenceListScore(vectorsResult.getData(), columnNameList);
        pcbSentenceVM.setPcbColumnSearchList(pcbColumnSearchVMList);

        // 전체 아이템 수와 빈 QueryColName 수를 계산
        int totalItems = pcbColumnSearchVMList.size();
        int emptyQueryColNameCount = 0;
        double totalScore = 0;
        int validScoreCount = 0;

        for (PcbColumnSearchVM pcbColumnSearchVM : pcbColumnSearchVMList) {
            if (StringUtils.isEmpty(pcbColumnSearchVM.getQueryColName())) {
                emptyQueryColNameCount++;
                continue;
            }
            totalScore += pcbColumnSearchVM.getQueryScore();
            validScoreCount++;
        }

        // 빈 값 비율 계산 (0.0 ~ 1.0)
        double emptyRatio = (double) emptyQueryColNameCount / totalItems;

        // 평균 점수 계산
        double averageScore = 0;
        if (validScoreCount > 0) {
            averageScore = totalScore / validScoreCount;

            // 빈 값 비율에 따라 평균 점수 감소
            // 예: 빈 값이 50%면 점수를 50% 감소
            averageScore = averageScore * (1.0 - emptyRatio);
        }

        pcbSentenceVM.setAverageScore(averageScore);
        return CCObjectResult.setSimpleData(pcbSentenceVM);
    }


    private List<PcbColumnSearchVM> searchSentenceListScore(List<List<Double>> queryVectorList, List<String> queryList) {
        List<PcbColumnSearchVM> pcbColumnSearchVMList = new ArrayList<>();
        MsearchRequest.Builder mBuilder = new MsearchRequest.Builder()
                .index(ElasticIndexName.PCB_COLUMN);

        for (List<Double> queryVector : queryVectorList) {
            ScriptScoreQuery scriptScoreQuery = getScriptScoreQuery(queryVector);
            mBuilder.searches(new RequestItem.Builder().header(new MultisearchHeader.Builder().build())
                    .body(new MultisearchBody.Builder()
                            .query(scriptScoreQuery._toQuery())
                            .size(1)
                            .build())
                    .build());
        }

        try {
            MsearchResponse<PcbColumnSearch> msearch = this.esClient.msearch(mBuilder.build(), PcbColumnSearch.class);
            for (int i = 0; i < msearch.responses().size(); i++) {
                MultiSearchResponseItem<PcbColumnSearch> responseItem = msearch.responses().get(i);
                MultiSearchItem<PcbColumnSearch> result = responseItem.result();
                List<Hit<PcbColumnSearch>> hits = result.hits().hits();
                Hit<PcbColumnSearch> pcbColumnSearchHit = hits.getFirst();
                if (pcbColumnSearchHit == null || pcbColumnSearchHit.source() == null) {
                    continue;
                }
                PcbColumnSearch pcbColumnSearch = pcbColumnSearchHit.source();
                String queryColName = queryList.get(i);
                PcbColumnSearchVM pcbColumnSearchVM = new PcbColumnSearchVM();
                BeanUtils.copyProperties(pcbColumnSearch, pcbColumnSearchVM);
                Double score = pcbColumnSearchHit.score();
                if (score != null && score > 1.4) {
                    double percentage = (score / 2.0) * 100;
                    double scorePercent = Math.min(percentage, 100.0);
                    pcbColumnSearchVM.setQueryScore(scorePercent);
                } else {
                    pcbColumnSearchVM.setQueryScore(0.0);
                }
                pcbColumnSearchVM.setQueryColName(queryColName);
                pcbColumnSearchVMList.add(pcbColumnSearchVM);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return pcbColumnSearchVMList;
    }

    private ScriptScoreQuery getScriptScoreQuery(List<Double> queryVector) {
        Map<String, JsonData> params = new HashMap<>();
        params.put("query_vector", JsonData.of(queryVector));

        Script inlineScript = new Script.Builder()
                .lang("painless")
                .source("cosineSimilarity(params.query_vector, '" + PcbColumnSearchField.COL_NAME_VECTOR + "') + 1.0")
                .params(params)
                .build();

        return new ScriptScoreQuery.Builder()
                .query(new MatchAllQuery.Builder().build()._toQuery())
                .script(inlineScript)
                .build();
    }

    // 임시로 사용하는 메서드
    /*private Script createCosineScript(List<Double> queryVector, String query) {
        ScriptScoreQuery scriptScoreQuery = this.getScriptScoreQuery(queryVector);
        SearchRequest searchRequest = new SearchRequest.Builder()
                .index(ElasticIndexName.PCB_COLUMN)
                .query(scriptScoreQuery._toQuery())
                .size(1)
                .build();

        try {
//            SearchResponse<Map> searchStr = this.esClient.search(searchRequest, Map.class);
//            System.out.println(searchStr);
            SearchResponse<PcbColumnSearch> search = this.esClient.search(searchRequest, PcbColumnSearch.class);
            List<Hit<PcbColumnSearch>> hits = search.hits().hits();
            System.out.println("### query : " + query);
            for (Hit<PcbColumnSearch> hit : hits) {
                System.out.println(hit.source().getColName() + ", score : " + hit.score());
            }
            System.out.println("----------------");

//            List<Map> list = (List<Map>) SearchHitSupport.unwrapSearchHits();
//            Map source = (Map) ((Hit) search.hits().hits().get(0)).source();
//            System.out.println("#################### " + query + " @@@@@@@@ " + source.get("colName"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return null;
    }*/

    public CCResult indexing(PcbColumnSearchVM pcbColumnSearchVM) {

        Criteria criteria = new Criteria(PcbColumnSearchField.COL_NAME_KEYWORD).is(pcbColumnSearchVM.getColName());
        Query query = new CriteriaQuery(criteria);
        long count = this.elasticsearchOperations.count(query, PcbColumnSearch.class);
        if (count != 0) {
            return new CCResult.Builder()
                    .setFailMessage("동일한 컬렴이 존재합니다.")
                    .build();
        }

        PcbColumnSearch pcbColumnSearch = new PcbColumnSearch();
        BeanUtils.copyProperties(pcbColumnSearchVM, pcbColumnSearch);

        Mono<CCObjectResult<List<Double>>> encodedSentence = this.googleTensorService.getEncodedSentence(pcbColumnSearchVM.getColName());
        CCObjectResult<List<Double>> encodedVectorResult = encodedSentence.block();
        if (encodedVectorResult != null) {
            pcbColumnSearch.setColNameVector(encodedVectorResult.getData());
        }
        return CCObjectResult.setSimpleData(this.pcbColumnSearchRepository.save(pcbColumnSearch));
    }

}
