package com.ncst.job.portal.security;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import com.ncst.job.portal.service.CustomUserDetailsService; // update to your actual package/name
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthFilter.class);

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

    private final CustomUserDetailsService userDetailsService;
    private final JwtTokenHelper jwtTokenHelper;

    public JwtAuthFilter(CustomUserDetailsService userDetailsService,
                         JwtTokenHelper jwtTokenHelper) {
        this.userDetailsService = userDetailsService;
        this.jwtTokenHelper = jwtTokenHelper;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        // Use request.getRequestURI() since swagger UI may use servlet path + query etc.
        String path = request.getRequestURI(); 
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        for (String pattern : skipPaths) {
            if (pathMatcher.match(pattern, path)) {
                logger.debug("shouldNotFilter MATCH: pattern='{}' path='{}' -> skipping filter", pattern, path);
                return true;
            }
        }
        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        logger.debug("JWT Filter running for path='{}' method='{}' authHeaderPresent={}",
                request.getRequestURI(), request.getMethod(),
                request.getHeader("Authorization") != null);

        // Safe token processing: catch parsing exceptions and do NOT send error responses here
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                String username = jwtTokenHelper.getUsername(token);
                if (username != null && org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication() == null) {
                    var userDetails = (org.springframework.security.core.userdetails.UserDetails) userDetailsService.loadUserByUsername(username);
                    if (jwtTokenHelper.validate(token, userDetails)) {
                        var auth = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                        auth.setDetails(new org.springframework.security.web.authentication.WebAuthenticationDetailsSource().buildDetails(request));
                        org.springframework.security.core.context.SecurityContextHolder.getContext().setAuthentication(auth);
                        logger.debug("JWT Filter: authenticated user='{}'", username);
                    } else {
                        logger.debug("JWT Filter: token validation failed for user='{}'", username);
                    }
                }
            } else {
                logger.debug("JWT Filter: no bearer token");
            }
        } catch (Exception ex) {
            // Log and continue — do not call response.sendError(...) here
            logger.warn("JWT parse/validate exception (continuing unauthenticated): {}", ex.toString());
        }

        filterChain.doFilter(request, response);
    }
}

