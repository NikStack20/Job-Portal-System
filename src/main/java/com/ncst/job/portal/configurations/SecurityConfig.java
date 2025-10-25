package com.ncst.job.portal.configurations;
import org.springframework.beans.factory.annotation.Autowired;  
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.ncst.job.portal.security.JwtAuthFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    //  Main security filter chain
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable()) // for APIs (dev); configure properly for prod if needed
            .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Explicitly permit the OpenAPI JSON (GET) and its subpaths
                .requestMatchers(HttpMethod.GET, "/v3/api-docs", "/v3/api-docs/**").permitAll()

                // Swagger UI + webjars + resources
                .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/swagger-ui/index.html",
                                 "/swagger-resources/**", "/webjars/**").permitAll()

                // Allow OPTIONS (CORS preflight)
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // Auth endpoints (login/register)
                .requestMatchers("/api/auth/**", "/api/users/register").permitAll()

                // Public read-only job endpoints (if you want only GET public, restrict others later)
                .requestMatchers(HttpMethod.GET, "/api/jobs/**").permitAll()

                // everything else needs authentication
                .anyRequest().authenticated()
            )
            // Add JWT filter (ensure this filter is implemented to skip swagger/docs)
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}