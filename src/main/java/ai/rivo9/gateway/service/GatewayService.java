package ai.rivo9.gateway.service;

import ai.rivo9.gateway.dto.BrandRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
public class GatewayService {

    private final RestTemplate restTemplate;

    @Value("${backend.api.base-url}")
    private String backendBaseUrl;

    public ResponseEntity<String> forwardToRivoFetch(BrandRequest request, String apiKey, String customOrigin) {
        String url = backendBaseUrl + "/api/secure/rivofetch";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-api-key", apiKey);
        if (customOrigin != null && !customOrigin.isEmpty()) {
            headers.set("Origin", customOrigin);
        }

        HttpEntity<BrandRequest> entity = new HttpEntity<>(request, headers);
        
        log.info("Forwarding to /rivofetch: url={}, apiKey={}", request.getUrl(), maskApiKey(apiKey));
        
        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            log.info("Response from /rivofetch: status={}", response.getStatusCode());
            return response;
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            log.error("Error from /rivofetch: status={}, body={}", e.getStatusCode(), e.getResponseBodyAsString());
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
        } catch (Exception e) {
            log.error("Unexpected error calling /rivofetch: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\":\"Gateway error: " + e.getMessage() + "\"}");
        }
    }

    public ResponseEntity<String> forwardToForward(BrandRequest request, String jwtToken) {
        String url = backendBaseUrl + "/forward";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + jwtToken);

        HttpEntity<BrandRequest> entity = new HttpEntity<>(request, headers);
        
        log.info("Forwarding to /forward: url={}, jwt={}", request.getUrl(), maskToken(jwtToken));
        
        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            log.info("Response from /forward: status={}", response.getStatusCode());
            return response;
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            log.error("Error from /forward: status={}, body={}", e.getStatusCode(), e.getResponseBodyAsString());
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
        } catch (Exception e) {
            log.error("Unexpected error calling /forward: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\":\"Gateway error: " + e.getMessage() + "\"}");
        }
    }

    private String maskApiKey(String apiKey) {
        if (apiKey == null || apiKey.length() < 8) return "***";
        return apiKey.substring(0, 4) + "..." + apiKey.substring(apiKey.length() - 4);
    }

    private String maskToken(String token) {
        if (token == null || token.length() < 20) return "***";
        return token.substring(0, 10) + "...";
    }
}
