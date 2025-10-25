package com.ncst.job.portal.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SwaggerSecurityConfig {

    @Bean
    @Order(0)
    SecurityFilterChain swaggerSecurityChain(HttpSecurity http) throws Exception {
        http
          .securityMatcher("/v3/api-docs/**", "/v3/api-docs", "/swagger-ui/**", "/swagger-ui.html", "/swagger-ui/index.html")
          .authorizeHttpRequests(a -> a.anyRequest().permitAll())
          .csrf(c -> c.disable())
          .formLogin(form -> form.disable())
          .httpBasic(b -> b.disable())
          .sessionManagement(s -> s.sessionCreationPolicy(org.springframework.security.config.http.SessionCreationPolicy.STATELESS));

        return http.build();
    }

}


