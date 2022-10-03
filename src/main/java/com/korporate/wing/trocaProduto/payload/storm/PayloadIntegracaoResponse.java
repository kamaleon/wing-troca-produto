package com.korporate.wing.trocaProduto.payload.storm;

import java.time.Instant;
import java.util.Map;

public class PayloadIntegracaoResponse {
    private String urlBase;
    private String lastToken;
    private Map<String, String> headersMap;

    private Instant generationTime;

    public String getUrlBase() {
        return urlBase;
    }

    public void setUrlBase(String urlBase) {
        this.urlBase = urlBase;
    }

    public String getLastToken() {
        return lastToken;
    }

    public void setLastToken(String lastToken) {
        this.lastToken = lastToken;
    }

    public Map<String, String> getHeadersMap() {
        return headersMap;
    }

    public void setHeadersMap(Map<String, String> headersMap) {
        this.headersMap = headersMap;
    }

    public Instant getGenerationTime() {
        return generationTime;
    }

    public void setGenerationTime(Instant generationTime) {
        this.generationTime = generationTime;
    }
}