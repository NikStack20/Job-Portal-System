package com.ncst.job.portal.security;
import java.io.IOException; 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.AntPathMatcher;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;

/**
 * JwtAuthenticationFilter:
 * - extracts Bearer token from Authorization header
 * - validates token using JwtTokenHelper
 * - sets Spring Security context if token valid
 *
 * Register this filter before UsernamePasswordAuthenticationFilter in SecurityConfig.
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthFilter.class);

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    // Paths to bypass (swagger/docs, public auth endpoints, static resources, actuator)
    private final List<String> skipPaths = List.of(
            "/api/auth/**",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-resources/**",
            "/swagger-ui.html",
            "/actuator/**",
            "/public/**",        // any static/public endpoints you might use
            "/files/**"          // if you expose resumes/files publicly
    );

    @Autowired
    private UserDetailsService userDetailsService; // your UserDetailsServiceImpl

    @Autowired
    private JwtTokenHelper jwtTokenHelper; // implement this (see contract below)

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) throws ServletException {
        // skip preflight requests immediately
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String path = request.getRequestURI();
        // if any skip pattern matches, do not filter (let other security config permit)
        for (String pattern : skipPaths) {
            if (pathMatcher.match(pattern, path)) {
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
            // no token or not Bearer: we allow request to proceed and SecurityConfig will block protected endpoints
            logger.debug("No Bearer token found in request");
        }

        // if username was extracted and user is not already authenticated in context
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

