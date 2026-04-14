package kr.co.samplepcb.xpse.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {

    private final CorsConfiguration cors = new CorsConfiguration();
    private final Jwt jwt = new Jwt();
    private final SpLinserver spLinserver = new SpLinserver();
    private final MlServer mlServer = new MlServer();
    private final Digikey digikey = new Digikey();
    private final UniKeyIC unikeyic = new UniKeyIC();
    private final ExternalCache externalCache = new ExternalCache();

    public CorsConfiguration getCors() {
        return cors;
    }

    public Jwt getJwt() {
        return jwt;
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

    public UniKeyIC getUnikeyic() {
        return unikeyic;
    }

    public ExternalCache getExternalCache() {
        return externalCache;
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

    public static class Jwt {
        private String secret;

        public String getSecret() {
            return secret;
        }

        public void setSecret(String secret) {
            this.secret = secret;
        }
    }

    public static class UniKeyIC {
        private String baseUrl;
        private String apiKey;
        private int exchangeRate = 1350;

        public String getBaseUrl() {
            return baseUrl;
        }

        public void setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
        }

        public String getApiKey() {
            return apiKey;
        }

        public void setApiKey(String apiKey) {
            this.apiKey = apiKey;
        }

        public int getExchangeRate() {
            return exchangeRate;
        }

        public void setExchangeRate(int exchangeRate) {
            this.exchangeRate = exchangeRate;
        }
    }

    public static class ExternalCache {
        private int ttlHours = 24;

        public int getTtlHours() {
            return ttlHours;
        }

        public void setTtlHours(int ttlHours) {
            this.ttlHours = ttlHours;
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
