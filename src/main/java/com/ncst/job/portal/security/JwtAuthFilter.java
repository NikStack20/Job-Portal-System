package com.ncst.job.portal.security;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthFilter.class);

    // Patterns we want to skip filtering for (swagger, api-docs, auth endpoints, webjars, etc)
    private final List<String> skipPaths = Arrays.asList(
            "/v3/api-docs",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/swagger-ui/index.html",
            "/swagger-resources/**",
            "/webjars/**",
            "/api/auth/**",
            "/api/users/register"
    );

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Autowired
    private CustomUserDetailsService userDetailsService; // or UserDetailsService

    @Autowired
    private JwtTokenHelper jwtTokenHelper; // your helper to parse/validate tokens

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) throws ServletException {
        // Always skip preflight
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // Build the request path to test against patterns (handles servlet context if any)
        String servletPath = request.getServletPath(); // e.g. "/v3/api-docs"
        String pathInfo = request.getPathInfo(); // maybe null
        String fullPath = (pathInfo != null) ? servletPath + pathInfo : servletPath;

        for (String pattern : skipPaths) {
            if (pathMatcher.match(pattern, fullPath)) {
                logger.debug("Skipping JWT filter for path: {} (pattern matched: {})", fullPath, pattern);
                return true;
            }
        }
        return false;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            try {
                username = jwtTokenHelper.getUsername(token);
            } catch (IllegalArgumentException ex) {
                logger.warn("Unable to get JWT token: {}", ex.getMessage());
            } catch (ExpiredJwtException ex) {
                logger.info("JWT token expired: {}", ex.getMessage());
            } catch (MalformedJwtException ex) {
                logger.warn("Malformed JWT token: {}", ex.getMessage());
            } catch (Exception ex) {
                logger.error("Error while parsing JWT token", ex);
            }
        } else {
            logger.debug("No Bearer token found in request: {} {}", request.getMethod(), request.getRequestURI());
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                if (jwtTokenHelper.validate(token, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    logger.debug("JWT authentication successful for user: {}", username);
                } else {
                    logger.warn("JWT token validation failed for user: {}", username);
                }
            } catch (Exception ex) {
                logger.error("Failed to set user authentication in security context", ex);
            }
        }

        filterChain.doFilter(request, response);
    }
}

