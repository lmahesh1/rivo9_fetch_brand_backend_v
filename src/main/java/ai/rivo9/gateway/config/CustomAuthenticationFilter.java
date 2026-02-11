package ai.rivo9.gateway.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

public class CustomAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String path = request.getRequestURI();
        
        // Skip authentication for public endpoints
        if (path.equals("/api/health") || path.startsWith("/actuator/")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        // Authenticate based on endpoint
        if (path.startsWith("/api/secure/")) {
            String apiKey = request.getHeader("x-api-key");
            if (apiKey != null && !apiKey.trim().isEmpty()) {
                UsernamePasswordAuthenticationToken auth = 
                    new UsernamePasswordAuthenticationToken("api-key-user", apiKey, 
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        } else if (path.startsWith("/api/forward")) {
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                UsernamePasswordAuthenticationToken auth = 
                    new UsernamePasswordAuthenticationToken("jwt-user", authHeader, 
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }
        
        filterChain.doFilter(request, response);
    }
}
