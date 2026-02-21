package order_service.config.security_config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.*;
import java.util.function.Supplier;

@EnableWebSecurity
@Configuration
public class ResourceServerConfig {

    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-uri}")
    private String jwtUri;

    /**
     * SINGLE SecurityFilterChain - ALL endpoints configured here
     * ✅ No securityMatcher, no multiple chains, super clean!
     */
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // ✅ Client credentials ONLY for /exists
                        .requestMatchers("/api/v1/order/exists").access(this::isValidPaymentServiceClient)

                        // ✅ PKCE user endpoints - exact paths
                        .requestMatchers(HttpMethod.POST, "/api/v1/order/place").hasRole("USER")
                        .requestMatchers(HttpMethod.POST, "/api/v1/order/updateOrderEvent").hasRole("USER")
                        .requestMatchers(HttpMethod.GET, "/api/v1/order/getOrders/**").hasRole("USER")

                        // ✅ Fallback - all other authenticated requests
                        .anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .jwtAuthenticationConverter(jwtAuthenticationConverter())
                                .jwkSetUri(jwtUri)))
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> extractAuthorities(jwt));
        return converter;
    }

    private Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
        Set<GrantedAuthority> authorities = new HashSet<>();

        Map<String, Object> realmAccess = jwt.getClaim("realm_access");
        if (realmAccess != null && realmAccess.get("roles") instanceof Collection<?> roles) {
            for (Object role : roles) {
                authorities.add(new SimpleGrantedAuthority("ROLE_" + role.toString().toUpperCase()));
            }
        }

        Map<String, Object> resourceAccess = jwt.getClaim("resource_access");
        if (resourceAccess != null) {
            resourceAccess.forEach((client, value) -> {
                if (value instanceof Map<?, ?> clientMap) {
                    Object clientRoles = clientMap.get("roles");
                    if (clientRoles instanceof Collection<?> roles) {
                        for (Object role : roles) {
                            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.toString().toUpperCase()));
                        }
                    }
                }
            });
        }

        return authorities;
    }

    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(
                "http://localhost:5173",
                "https://nn0qd5xp-5173.inc1.devtunnels.ms",
                "https://192.168.1.5:5173"
            ));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    /**
     * Client credentials validation - payment-service only
     */
    private AuthorizationDecision isValidPaymentServiceClient(
            Supplier<Authentication> authenticationSupplier,
            RequestAuthorizationContext requestAuthorizationContext) {

        Authentication authentication = authenticationSupplier.get();

        if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
            String azp = jwt.getClaimAsString("azp");
            return new AuthorizationDecision("payment-service".equals(azp));
        }
        return new AuthorizationDecision(false);
    }
}
