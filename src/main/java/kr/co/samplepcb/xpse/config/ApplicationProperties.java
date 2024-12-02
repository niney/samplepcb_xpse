package kr.co.samplepcb.xpse.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {

    private final CorsConfiguration cors = new CorsConfiguration();
    private final SpLinserver spLinserver = new SpLinserver();
    private final MlServer mlServer = new MlServer();
    private final Digikey digikey = new Digikey();
    public CorsConfiguration getCors() {
        return cors;
    }

    public SpLinserver getSpLinserver() {
        return spLinserver;
    }

    public MlServer getMlServer() {
        return mlServer;
    }

    public Digikey getDigikey() {
        return digikey;
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

    public static class MlServer {
        private String serverUrl;

        public String getServerUrl() {
            return serverUrl;
        }

        public void setServerUrl(String serverUrl) {
            this.serverUrl = serverUrl;
        }
    }

    public static class Digikey {
        private String baseUrl;
        private String clientId;
        private String clientSecret;

        public String getBaseUrl() {
            return baseUrl;
        }

        public void setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
        }

        public String getClientId() {
            return clientId;
        }

        public void setClientId(String clientId) {
            this.clientId = clientId;
        }

        public String getClientSecret() {
            return clientSecret;
        }

        public void setClientSecret(String clientSecret) {
            this.clientSecret = clientSecret;
        }
    }
}
