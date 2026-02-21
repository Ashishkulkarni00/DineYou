package restaurant_service.config.security_config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.function.Supplier;

@Configuration
@EnableWebSecurity
public class ResourceServerConfig {

    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-uri}")
    private String jwtUri;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // ✅ Fixed CORS
                .csrf(csrf -> csrf.disable()) // ✅ Disable CSRF for REST APIs
                .authorizeHttpRequests(auth -> auth
                        // Public GET endpoint
                        .requestMatchers(
                                HttpMethod.GET, "/api/v1/restaurant/{id}",
                                "/api/v1/restaurantLandingPage/getDetails/{restaurantId}",
                                "/uploads/**")
                        .permitAll()

                        // Protected endpoints (ADMIN)
                        .requestMatchers(HttpMethod.POST, "/api/v1/restaurant/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/restaurant/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/restaurant/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/restaurant/all").hasRole("ADMIN")

                        // Client-to-client endpoint remains restricted by your isValidClient() method
                        .requestMatchers("/api/v1/restaurant/{restaurantId}/exists").access(this::isValidClient)

                        // All other requests — deny or handle as needed
                        .anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwkSetUri(jwtUri)));

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOriginPatterns(Arrays.asList("*"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(Arrays.asList("*"));
        config.setExposedHeaders(Arrays.asList("*"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");
        grantedAuthoritiesConverter.setAuthoritiesClaimName("realm_access.roles");
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return converter;
    }

    private AuthorizationDecision isValidClient(
            Supplier<Authentication> authenticationSupplier,
            RequestAuthorizationContext requestAuthorizationContext) {

        Authentication authentication = authenticationSupplier.get();

        if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
            String clientId = jwt.getClaimAsString("client_id");
            boolean granted = "payment-service".equals(clientId) || "order-service".equals(clientId)
                    || "cart-service".equals(clientId);
            return new AuthorizationDecision(granted);
        }

        return new AuthorizationDecision(false);
    }
}