package kr.co.samplepcb.xpse.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.SearchHitSupport;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.index.PutIndexTemplateRequest;
import org.springframework.data.elasticsearch.core.index.Settings;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

/**
 * CoolElasticUtils 클래스는 Elasticsearch 인덱스의 생성 및 업데이트를 돕는 유틸리티 메서드를 제공합니다.
 */
public class CoolElasticUtils {

    private static final Logger logger = LoggerFactory.getLogger(CoolElasticUtils.class);
    private static final String ENTITY_PACKAGE = "kr.co.samplepcb.xpse.domain";

    /**
     * Elasticsearch 인덱스를 처리합니다. 인덱스가 존재할 경우 업데이트를 수행하고, 존재하지 않을 경우 새로 생성합니다.
     *
     * @param resource 인덱스 설정 정보를 포함한 Resource 객체
     * @param elasticsearchOperations Elasticsearch 작업을 수행하는 ElasticsearchOperations 객체
     * @param indexName 처리할 인덱스의 이름
     */
    public static void processIndex(Resource resource, ElasticsearchOperations elasticsearchOperations, String indexName) {
        IndexCoordinates indexCoordinates = IndexCoordinates.of(indexName);
        IndexOperations indexOps = elasticsearchOperations.indexOps(indexCoordinates);

        try {
            Class<?> entityClass = findEntityClassForIndex(indexName);
            if (entityClass == null) {
                logger.error("No entity class found for index: {}", indexName);
                return;
            }

            boolean exists = indexOps.exists();
            if (exists) {
                updateExistingIndex(indexName, indexOps, entityClass);
            } else {
                createNewIndex(resource, indexName, indexOps, entityClass);
            }
        } catch (Exception e) {
            logger.error("Error processing index: {}", indexName, e);
        }
    }

    /**
     * 주어진 인덱스 이름에 해당하는 엔티티 클래스를 검색합니다.
     *
     * @param indexName 검색할 인덱스의 이름
     * @return 인덱스 이름에 해당하는 엔티티 클래스, 찾을 수 없을 경우 null 반환
     * @throws IOException 리소스를 읽는 도중 발생할 수 있는 I/O 예외
     * @throws ClassNotFoundException 클래스 파일을 찾을 수 없는 경우 발생할 수 있는 예외
     */
    private static Class<?> findEntityClassForIndex(String indexName) throws IOException, ClassNotFoundException {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(resolver);

        String packageSearchPath = "classpath*:" + ENTITY_PACKAGE.replace('.', '/') + "/**/*.class";
        Resource[] resources = resolver.getResources(packageSearchPath);

        for (Resource resource : resources) {
            if (resource.isReadable()) {
                MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
                String className = metadataReader.getClassMetadata().getClassName();
                Class<?> clazz = Class.forName(className);

                Document document = clazz.getAnnotation(Document.class);
                if (document != null && document.indexName().equals(indexName)) {
                    logger.info("Found entity class {} for index: {}", className, indexName);
                    return clazz;
                }
            }
        }
        logger.warn("No entity class found for index: {}", indexName);
        return null;
    }

    /**
     * 기존 Elasticsearch 인덱스의 맵핑을 업데이트하는 메서드입니다.
     *
     * @param indexName 업데이트할 인덱스의 이름
     * @param indexOps 인덱스 작업을 수행하는 IndexOperations 객체
     * @param entityClass 인덱스와 연결된 엔티티 클래스
     */
    public static void updateExistingIndex(String indexName, IndexOperations indexOps, Class<?> entityClass) {
        try {
            org.springframework.data.elasticsearch.core.document.Document document = indexOps.createMapping(entityClass);
            indexOps.putMapping(document);
            logger.info("Updated mapping for existing index: {}", indexName);
        } catch (Exception e) {
            logger.error("Error updating mapping for index: {}", indexName, e);
        }
    }

    /**
     * 주어진 설정 정보를 기반으로 새로운 Elasticsearch 인덱스를 생성하는 메서드입니다.
     * 인덱스 템플릿을 적용하고, 인덱스를 생성합니다.
     *
     * @param resource 인덱스 설정 정보를 포함한 Resource 객체
     * @param indexName 생성할 인덱스의 이름
     * @param indexOps 인덱스 작업을 수행하는 IndexOperations 객체
     * @param entityClass 인덱스와 연결된 엔티티 클래스
     * @throws IOException 파일을 읽는 도중 발생할 수 있는 예외
     * @throws JSONException JSON 파싱 도중 발생할 수 있는 예외
     */
    public static void createNewIndex(Resource resource, String indexName, IndexOperations indexOps, Class<?> entityClass) throws IOException, JSONException {
        String configJson = readFileContentFirstIgnore(resource);
        JSONObject config = new JSONObject(configJson);

        PutIndexTemplateRequest request = PutIndexTemplateRequest.builder()
                .withName(indexName + "-template")
                .withIndexPatterns(indexName)
                .withSettings(Settings.parse(config.getJSONObject("settings").toString()))
                .withMapping(org.springframework.data.elasticsearch.core.document.Document.parse(config.getJSONObject("mappings").toString()))
                .build();

        boolean success = indexOps.putIndexTemplate(request);

        if (success) {
            logger.info("Index template applied and index created: {}", indexName);
        } else {
            logger.error("Failed to apply index template for index: {}", indexName);
        }
    }

    /**
     * 주어진 Resource 객체에서 파일 콘텐츠를 읽어와서 문자열로 반환합니다.
     *
     * @param resource 읽을 파일이 포함된 Resource 객체
     * @return 파일의 콘텐츠를 문자열로 반환
     * @throws IOException 파일을 읽는 도중 발생할 수 있는 예외
     */
    public static String readFileContent(Resource resource) throws IOException {
        byte[] contentBytes = resource.getInputStream().readAllBytes();
        return new String(contentBytes, StandardCharsets.UTF_8);
    }

    /**
     * 주어진 Resource 객체에서 첫 번째 줄을 건너뛰고 파일 콘텐츠를 읽어와 문자열로 반환합니다.
     *
     * @param resource 읽을 파일이 포함된 Resource 객체
     * @return 첫 번째 줄을 제외한 파일의 콘텐츠를 문자열로 반환
     * @throws IOException 파일을 읽는 도중 발생할 수 있는 예외
     */
    public static String readFileContentFirstIgnore(Resource resource) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            // Skip the first line
            reader.readLine();

            // Read the rest of the file
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }

    /**
     * 주어진 SearchHits 객체에서 실제 검색 결과 리스트를 추출하는 메서드입니다.
     *
     * @param <T> 검색 결과 엔티티의 타입
     * @param searchHits 검색 결과를 포함한 SearchHits 객체
     * @return 검색 결과 엔티티의 리스트
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> unwrapSearchHits(SearchHits<T> searchHits) {
        return (List<T>) SearchHitSupport.unwrapSearchHits(searchHits);
    }
}
