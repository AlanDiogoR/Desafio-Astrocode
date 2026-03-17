package com.astrocode.backend.domain.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.*;

@Component
public class PluggyApiClient {

    private static final Logger log = LoggerFactory.getLogger(PluggyApiClient.class);
    private static final String BASE_URL = "https://api.pluggy.ai";

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${pluggy.client-id:}")
    private String clientId;

    @Value("${pluggy.client-secret:}")
    private String clientSecret;

    private String cachedApiKey;
    private long apiKeyExpiresAt;

    public boolean isConfigured() {
        return clientId != null && !clientId.isBlank()
                && clientSecret != null && !clientSecret.isBlank();
    }

    public String createConnectToken(UUID userId, String oauthRedirectUri) {
        String apiKey = getOrRefreshApiKey();
        String url = BASE_URL + "/connect_token";

        Map<String, Object> body = new HashMap<>();
        Map<String, Object> options = new HashMap<>();
        options.put("clientUserId", userId.toString());
        options.put("avoidDuplicates", true);
        if (oauthRedirectUri != null && !oauthRedirectUri.isBlank()) {
            options.put("oauthRedirectUri", oauthRedirectUri);
        }
        body.put("options", options);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-API-KEY", apiKey);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new IllegalStateException("Falha ao criar connect token da Pluggy");
        }

        try {
            JsonNode node = objectMapper.readTree(response.getBody());
            JsonNode accessToken = node.get("accessToken");
            if (accessToken == null) {
                throw new IllegalStateException("Resposta Pluggy sem accessToken");
            }
            return accessToken.asText();
        } catch (Exception e) {
            log.error("Erro ao parsear connect token", e);
            throw new IllegalStateException("Erro ao processar connect token");
        }
    }

    public List<PluggyAccountDto> fetchAccounts(String itemId) {
        String apiKey = getOrRefreshApiKey();
        String url = BASE_URL + "/accounts?itemId=" + itemId;

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-API-KEY", apiKey);

        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            return List.of();
        }

        try {
            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode results = root.get("results");
            if (results == null || !results.isArray()) {
                return List.of();
            }

            List<PluggyAccountDto> accounts = new ArrayList<>();
            for (JsonNode acc : results) {
                String type = acc.has("type") ? acc.get("type").asText() : "";
                if (!"BANK".equals(type)) {
                    continue;
                }
                String id = acc.has("id") ? acc.get("id").asText() : null;
                String name = acc.has("name") ? acc.get("name").asText() : "Conta Pluggy";
                double balanceCentavos = acc.has("balance") ? acc.get("balance").asDouble() : 0;
                BigDecimal balance = BigDecimal.valueOf(balanceCentavos).divide(BigDecimal.valueOf(100), 2, java.math.RoundingMode.HALF_UP);
                accounts.add(new PluggyAccountDto(id, itemId, name, balance));
            }
            return accounts;
        } catch (Exception e) {
            log.error("Erro ao parsear accounts da Pluggy", e);
            return List.of();
        }
    }

    public PluggyItemDto fetchItem(String itemId) {
        String apiKey = getOrRefreshApiKey();
        String url = BASE_URL + "/items/" + itemId;

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-API-KEY", apiKey);

        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            return null;
        }

        try {
            JsonNode node = objectMapper.readTree(response.getBody());
            String connectorName = node.has("connector") && node.get("connector").has("name")
                    ? node.get("connector").get("name").asText() : null;
            return new PluggyItemDto(itemId, connectorName);
        } catch (Exception e) {
            log.error("Erro ao parsear item da Pluggy", e);
            return null;
        }
    }

    private synchronized String getOrRefreshApiKey() {
        if (cachedApiKey != null && System.currentTimeMillis() < apiKeyExpiresAt - 60000) {
            return cachedApiKey;
        }

        String url = BASE_URL + "/auth";
        Map<String, String> body = Map.of(
                "clientId", clientId,
                "clientSecret", clientSecret
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new IllegalStateException("Falha ao autenticar na Pluggy. Verifique CLIENT_ID e CLIENT_SECRET.");
        }

        try {
            JsonNode node = objectMapper.readTree(response.getBody());
            JsonNode apiKeyNode = node.get("apiKey");
            if (apiKeyNode == null) {
                throw new IllegalStateException("Resposta Pluggy sem apiKey");
            }
            cachedApiKey = apiKeyNode.asText();
            apiKeyExpiresAt = System.currentTimeMillis() + (2 * 60 * 60 * 1000);
            return cachedApiKey;
        } catch (Exception e) {
            log.error("Erro ao parsear apiKey da Pluggy", e);
            throw new IllegalStateException("Erro ao autenticar na Pluggy");
        }
    }

    public record PluggyAccountDto(String id, String itemId, String name, BigDecimal balance) {}
    public record PluggyItemDto(String itemId, String institutionName) {}
}
