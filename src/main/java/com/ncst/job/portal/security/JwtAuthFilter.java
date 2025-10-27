package com.ncst.job.portal.security;
import java.io.IOException; 
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
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

    private static final Logger log = LoggerFactory.getLogger(JwtAuthFilter.class);

    private final List<String> skipPatterns = List.of(
        "/v3/api-docs", "/v3/api-docs/**",
        "/swagger-ui/**", "/swagger-ui.html", "/swagger-ui/index.html",
        "/api/auth/**", "/api/users/register", "/webjars/**", "/swagger-resources/**"
    );

    private final AntPathMatcher matcher = new AntPathMatcher();

    private final CustomUserDetailsService userDetailsService;
    private final JwtTokenHelper jwtTokenHelper;

    public JwtAuthFilter(CustomUserDetailsService userDetailsService, JwtTokenHelper jwtTokenHelper) {
        this.userDetailsService = userDetailsService;
        this.jwtTokenHelper = jwtTokenHelper;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) return true;
        for (String pattern : skipPatterns) {
            if (matcher.match(pattern, path)) {
                log.debug("Skipping JWT filter for path: {}", path);
                return true;
            }
        }
        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        String authHeader = request.getHeader("Authorization");
        log.debug("JWTFilter running for path='{}' authHeaderPresent={}", path, authHeader != null);

        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7).trim();
                String username = jwtTokenHelper.getUsername(token); // may throw if invalid
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails ud = userDetailsService.loadUserByUsername(username);
                    if (jwtTokenHelper.validate(token, ud)) {
                        UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(ud, null, ud.getAuthorities());
                        auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(auth);
                        log.debug("Authenticated user '{}' via JWT", username);
                    } else {
                        log.debug("JWT validation failed for user '{}'", username);
                    }
                }
            }
        } catch (Exception ex) {
            // Log full exception for debugging (do not send error here)
            log.warn("JWT parse/validate exception (continuing unauthenticated): ", ex);
        }

        chain.doFilter(request, response);
    }
}


