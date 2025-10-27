package com.ncst.job.portal.configurations;
import org.springframework.context.annotation.Bean;  
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.ncst.job.portal.security.JwtAuthFilter;
import com.ncst.job.portal.service.CustomUserDetailsService;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final JwtAuthenticationEntryPoint entryPoint;
    private final CustomUserDetailsService uds;

    public SecurityConfig(CustomUserDetailsService uds, JwtAuthFilter f, JwtAuthenticationEntryPoint e) {
        this.uds = uds;
        this.jwtAuthFilter = f;
        this.entryPoint = e;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Use AuthenticationConfiguration to expose AuthenticationManager
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration cfg) throws Exception {
        return cfg.getAuthenticationManager();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .exceptionHandling(e -> e.authenticationEntryPoint(entryPoint))
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // public auth and register
                .requestMatchers("/api/auth/**", "/api/users/register").permitAll()
                // swagger / docs
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                // file download for resumes maybe require auth (change as needed)
                .requestMatchers("/files/**").permitAll() // adjust: allow or secure
                // allow upload-resume only to authenticated users
                .requestMatchers(HttpMethod.POST, "/api/jobs/upload-resume").authenticated()
                // all API endpoints require auth unless specifically permitted
                .requestMatchers("/api/**").authenticated()
                // app UI and static can remain permitted
                .anyRequest().permitAll()
            )
            // Add JWT filter BEFORE UsernamePasswordAuthenticationFilter
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .httpBasic(b -> b.disable())
            .formLogin(f -> f.disable());

        return http.build();
    }
}





