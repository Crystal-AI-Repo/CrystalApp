package com.lovelycatv.ai.crystalapp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author lovelycat
 * @version 1.0
 * @since 2025-04-09 17:30
 */
@Configuration
@ConfigurationProperties(prefix = "crystal")
public class CrystalAppSettings {
    private String frontBaseUrl;
    private OAuth2Settings oauth2;

    public String getFrontBaseUrl() {
        return frontBaseUrl;
    }

    public OAuth2Settings getOauth2() {
        return oauth2;
    }

    public void setFrontBaseUrl(String frontBaseUrl) {
        this.frontBaseUrl = frontBaseUrl;
    }

    public void setOauth2(OAuth2Settings oauth2) {
        this.oauth2 = oauth2;
    }

    public static class OAuth2Settings {
        private String clientId;
        private String clientSecret;
        private String authFrontBaseUrl;

        public String getClientId() {
            return clientId;
        }

        public String getClientSecret() {
            return clientSecret;
        }

        public String getAuthFrontBaseUrl() {
            return authFrontBaseUrl;
        }

        public void setClientId(String clientId) {
            this.clientId = clientId;
        }

        public void setClientSecret(String clientSecret) {
            this.clientSecret = clientSecret;
        }

        public void setAuthFrontBaseUrl(String authFrontBaseUrl) {
            this.authFrontBaseUrl = authFrontBaseUrl;
        }
    }

}
