package kr.co.samplepcb.xpse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.elasticsearch.config.EnableElasticsearchAuditing;

@SpringBootApplication
@EnableElasticsearchAuditing
public class SamplepcbXpseApplication {

    public static void main(String[] args) {
        SpringApplication.run(SamplepcbXpseApplication.class, args);
    }

}
