package kr.co.samplepcb.xpse.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {

    private final CorsConfiguration cors = new CorsConfiguration();
    private final SpLinserver spLinserver = new SpLinserver();

    public CorsConfiguration getCors() {
        return cors;
    }

    public SpLinserver getSpLinserver() {
        return spLinserver;
    }

    public static class SpLinserver {
        private String serverUrl;

        public String getServerUrl() {
            return serverUrl;
        }

        public void setServerUrl(String serverUrl) {
            this.serverUrl = serverUrl;
        }
    }
}
