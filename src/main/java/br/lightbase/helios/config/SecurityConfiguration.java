package br.lightbase.helios.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

import br.lightbase.helios.config.jwt.JwtAuthenticationFilter;

@Configuration
// @EnableWebSecurity
public class SecurityConfiguration {
 
    private final JwtAuthenticationFilter jwtFilter;

    SecurityConfiguration(JwtAuthenticationFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }
    
    @Bean
    public SecurityWebFilterChain filterChain(ServerHttpSecurity http) {
        http
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .authorizeExchange(exchange -> exchange
                // .pathMatchers("/auth/**").permitAll()
                // .anyExchange().authenticated())
                .anyExchange().permitAll())
            .addFilterBefore(jwtFilter, SecurityWebFiltersOrder.AUTHENTICATION);
        return http.build();
    }
}
