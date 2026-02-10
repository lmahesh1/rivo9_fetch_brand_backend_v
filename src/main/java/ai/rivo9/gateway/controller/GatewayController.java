package ai.rivo9.gateway.controller;

import ai.rivo9.gateway.dto.BrandRequest;
import ai.rivo9.gateway.service.GatewayService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class GatewayController {

    private final GatewayService gatewayService;

    @PostMapping("/secure/rivofetch")
    public ResponseEntity<String> rivoFetch(
            @Valid @RequestBody BrandRequest request,
            @RequestHeader(value = "x-api-key", required = false) String apiKey,
            @RequestHeader(value = "X-Custom-Origin", required = false) String customOrigin,
            HttpServletRequest httpRequest) {
        
        log.info("Gateway received /rivofetch request: url={}", request.getUrl());
        
        if (apiKey == null || apiKey.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("{\"error\":\"API key is required in x-api-key header\"}");
        }

        return gatewayService.forwardToRivoFetch(request, apiKey, customOrigin);
    }

    @PostMapping("/forward")
    public ResponseEntity<String> forward(
            @Valid @RequestBody BrandRequest request,
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            HttpServletRequest httpRequest) {
        
        log.info("Gateway received /forward request: url={}", request.getUrl());
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("{\"error\":\"JWT Bearer token is required in Authorization header\"}");
        }

        String jwtToken = authHeader.substring(7);
        return gatewayService.forwardToForward(request, jwtToken);
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("{\"status\":\"UP\",\"service\":\"RIVO9 Gateway\"}");
    }
}
