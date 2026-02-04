package ai.rivo9.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            // VERY IMPORTANT: enable CORS support
            .cors()
            .and()
            // Disable CSRF for APIs
            .csrf().disable()
            // Allow all requests for now
            .authorizeHttpRequests(auth -> auth
                // Allow preflight requests explicitly
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .anyRequest().permitAll()
            )
            // Disable default login page & basic auth
            .httpBasic().disable()
            .formLogin().disable();

        return http.build();
    }
}
