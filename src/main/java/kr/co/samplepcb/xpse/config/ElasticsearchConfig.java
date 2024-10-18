package kr.co.samplepcb.xpse.config;

import jakarta.annotation.PostConstruct;
import kr.co.samplepcb.xpse.util.CoolElasticUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;

import java.io.IOException;

@Configuration
public class ElasticsearchConfig {

    private static final Logger logger = LoggerFactory.getLogger(ElasticsearchConfig.class);
    private final ElasticsearchOperations elasticsearchOperations;

    public ElasticsearchConfig(ElasticsearchOperations elasticsearchOperations) {
        this.elasticsearchOperations = elasticsearchOperations;
    }

    @PostConstruct
    public void initializeElasticsearch() throws IOException {
        Resource[] resources = new PathMatchingResourcePatternResolver().getResources("classpath:mapping/*.txt");

        for (Resource resource : resources) {
            String fileName = resource.getFilename();
            if (fileName == null) continue;

            String indexName = fileName.replace(".txt", "");
            CoolElasticUtils.processIndex(resource, elasticsearchOperations, indexName);
        }
    }

}
