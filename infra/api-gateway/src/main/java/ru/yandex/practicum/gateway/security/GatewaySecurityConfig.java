package ru.yandex.practicum.gateway.security;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.header.XFrameOptionsServerHttpHeadersWriter;
import org.springframework.security.web.server.header.XXssProtectionServerHttpHeadersWriter;

import static ru.yandex.practicum.gateway.security.UserRoles.ADMIN;
import static ru.yandex.practicum.gateway.security.UserRoles.USER;

@Configuration
@EnableWebFluxSecurity
@EnableConfigurationProperties(GatewayUsersProperties.class)
public class GatewaySecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public ReactiveUserDetailsService userDetailsService(
            GatewayUsersProperties usersProperties,
            PasswordEncoder passwordEncoder) {
        return new GatewayUserDetailsService(usersProperties, passwordEncoder);
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
            // Magic from baeldung and spring docs
            .headers(header -> header
                .xssProtection(xss ->
                    xss.headerValue(XXssProtectionServerHttpHeadersWriter.HeaderValue.ENABLED_MODE_BLOCK)
                )
                .contentSecurityPolicy(contentSecurityPolicy -> contentSecurityPolicy
                    .policyDirectives("script-src 'self'")
                )
                .frameOptions(frame -> frame
                    .mode(XFrameOptionsServerHttpHeadersWriter.Mode.SAMEORIGIN)
                )
            )
            .csrf(ServerHttpSecurity.CsrfSpec::disable)

            // Насколько я прочитал, security обрабатывается раньше, чем
            // gateway filter rewrite, но web-ui запрашивает сразу /api
            .authorizeExchange(exchange -> exchange
                    // Products: /products/** + /api/products/**
                    .pathMatchers(HttpMethod.GET, "/products/**", "/api/products/**").permitAll()
                    .pathMatchers(HttpMethod.POST, "/products/**", "/api/products/**").hasRole(ADMIN.name())
                    .pathMatchers(HttpMethod.PUT, "/products/**", "/api/products/**").hasRole(ADMIN.name())
                    .pathMatchers(HttpMethod.PATCH, "/products/**", "/api/products/**").hasRole(ADMIN.name())
                    .pathMatchers(HttpMethod.DELETE, "/products/**", "/api/products/**").hasRole(ADMIN.name())
                    // Categories: /categories/** + /api/categories/**
                    .pathMatchers(HttpMethod.GET, "/categories/**", "/api/categories/**").permitAll()
                    .pathMatchers(HttpMethod.POST, "/categories/**", "/api/categories/**").hasRole(ADMIN.name())
                    .pathMatchers(HttpMethod.PUT, "/categories/**", "/api/categories/**").hasRole(ADMIN.name())
                    .pathMatchers(HttpMethod.PATCH, "/categories/**", "/api/categories/**").hasRole(ADMIN.name())
                    .pathMatchers(HttpMethod.DELETE, "/categories/**", "/api/categories/**").hasRole(ADMIN.name())
                    // Inventory: /inventory/** + /api/inventory/**
                    .pathMatchers(HttpMethod.GET, "/inventory/**", "/api/inventory/**").permitAll()
                    .pathMatchers(HttpMethod.POST, "/inventory/**", "/api/inventory/**").hasRole(ADMIN.name())
                    .pathMatchers(HttpMethod.PUT, "/inventory/**", "/api/inventory/**").hasRole(ADMIN.name())
                    .pathMatchers(HttpMethod.PATCH, "/inventory/**", "/api/inventory/**").hasRole(ADMIN.name())
                    .pathMatchers(HttpMethod.DELETE, "/inventory/**", "/api/inventory/**").hasRole(ADMIN.name())
                    // Orders: /orders + /api/orders
                    .pathMatchers(HttpMethod.GET, "/orders", "/api/orders").hasRole(ADMIN.name())
                    .pathMatchers(HttpMethod.POST, "/orders/**", "/api/orders/**").hasRole(USER.name())
                    .pathMatchers(HttpMethod.GET, "/orders/{id}", "/api/orders/{id}").hasRole(USER.name())
                    .pathMatchers(HttpMethod.GET, "/orders/by-email", "/api/orders/by-email").hasRole(USER.name())

                    // Global
                    .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                    .pathMatchers(HttpMethod.GET, "/actuator/health").permitAll()
                    .pathMatchers(HttpMethod.GET, "/swagger-ui/**", "/openapi/**").permitAll()
                    .anyExchange().denyAll()
            )
            .httpBasic(Customizer.withDefaults());

        return http.build();
    }

}
