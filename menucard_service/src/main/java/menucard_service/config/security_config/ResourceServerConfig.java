package menucard_service.config.security_config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.function.Supplier;

@EnableWebSecurity
@Configuration
public class ResourceServerConfig {

    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-uri}")
    private String jwtUri;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/v1/menucard/restaurant/{restaurantId}",
                                "/api/v1/menucardItem/getPopularItems/{restaurantId}",
                                "/uploads/**",
                                "/api/v1/search/item/**"
                        ).permitAll()
                        .requestMatchers(
                                "/api/v1/menucardItem/getMenuItemsByIds",
                                "/api/v1/menucardItem",
                                "/api/v1/menucardItem/getPopularItems/**",
                                "/api/v1/menucardItem/{id}"
                        ).access(this::isValidClient)
                        .anyRequest().hasRole("ADMIN")
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwkSetUri(jwtUri))
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOriginPattern("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");

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

            boolean granted = "order-service".equals(clientId)
                    || "cart-service".equals(clientId)
                    || "restaurant-service".equals(clientId);

            return new AuthorizationDecision(granted);
        }
        return new AuthorizationDecision(false);
    }
}

