package cart_service.config.security_config;

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
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

@EnableWebSecurity
@Configuration
public class ResourceServerConfig {

        @Value("${spring.security.oauth2.resourceserver.jwt.jwk-uri}")
        private String jwtUri;

        @Bean
        SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                .cors().and()
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers(HttpMethod.GET, "/api/v1/cart/{userId}")
                                                .hasRole("USER")
                                                .requestMatchers(HttpMethod.POST, "/api/v1/cart/create")
                                                .hasRole("USER")
                                                .requestMatchers(HttpMethod.POST, "/api/v1/cartItem/addCartItem")
                                                .hasRole("USER")
                                                .requestMatchers(HttpMethod.DELETE, "/api/v1/cartItem/{id}")
                                                .hasRole("USER")
                                                .requestMatchers(HttpMethod.GET, "/api/v1/cart/calculateTotal/{id}")
                                                .hasAnyRole("USER")
                                                .requestMatchers(
                                                                "/api/v1/cart/validate-cart-and-menu-items",
                                                                "/api/v1/cart/updateCartStatus")
                                        .access(this::isValidClient)
                                                .anyRequest().hasRole("ADMIN"))
                                .oauth2ResourceServer(oauth2 -> oauth2
                                                .jwt(jwt -> jwt
                                                                .jwtAuthenticationConverter(
                                                                                jwtAuthenticationConverter())
                                                                .jwkSetUri(jwtUri)));

                return http.build();
        }

        @Bean
        public JwtAuthenticationConverter jwtAuthenticationConverter() {
                JwtAuthenticationConverter converter = new JwtAuthenticationConverter();

                converter.setJwtGrantedAuthoritiesConverter(jwt -> (Collection<GrantedAuthority>) extractAuthorities(jwt));

                return converter;
        }

        private Collection<? extends GrantedAuthority> extractAuthorities(Jwt jwt) {
                Set<GrantedAuthority> authorities = new HashSet<>();

                Map<String, Object> realmAccess = jwt.getClaim("realm_access");
                if (realmAccess != null && realmAccess.get("roles") instanceof Collection<?> roles) {
                        for (Object role : roles) {
                                authorities.add(new SimpleGrantedAuthority("ROLE_" + role.toString().toUpperCase()));
                        }
                }

                // ✅ Extract resource (client) roles if any
                Map<String, Object> resourceAccess = jwt.getClaim("resource_access");
                if (resourceAccess != null) {
                        resourceAccess.forEach((client, access) -> {
                                if (access instanceof Map<?, ?> accessMap) {
                                        Object clientRoles = accessMap.get("roles");
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


        
        private AuthorizationDecision isValidClient(
                        Supplier<Authentication> authenticationSupplier,
                        RequestAuthorizationContext requestAuthorizationContext) {

                Authentication authentication = authenticationSupplier.get();

                if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
                        String clientId = jwt.getClaimAsString("client_id");
                        boolean granted = "order-service".equals(clientId);
                        return new AuthorizationDecision(granted);
                }
                return new AuthorizationDecision(false);
        }

}