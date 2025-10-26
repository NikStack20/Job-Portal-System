package com.ncst.job.portal.controllers;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ncst.job.portal.loadouts.LoginRequest;
import com.ncst.job.portal.security.JwtTokenHelper;
import com.ncst.job.portal.service.CustomUserDetailsService;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final JwtTokenHelper jwtTokenHelper;
    private final org.slf4j.Logger log = LoggerFactory.getLogger(getClass());

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req, BindingResult br) {
        // Validation errors from @Valid
        if (br.hasErrors()) {
            String msg = br.getAllErrors().stream()
                           .map(ObjectError::getDefaultMessage)
                           .collect(Collectors.joining("; "));
            log.debug("Login validation failed: {}", msg);
            return ResponseEntity.badRequest().body(Map.of("error", msg));
        }

        log.debug("Login attempt for email='{}' (password set? {})", req.getEmail(), req.getPassword() != null);

        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword())
            );
            UserDetails ud = (UserDetails) auth.getPrincipal();
            String token = jwtTokenHelper.generateToken(ud);

            Map<String,Object> body = Map.of(
                "token", token,
                "tokenType", "Bearer",
                "username", ud.getUsername()
            );
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);

            return ResponseEntity.ok().headers(headers).body(body);

        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error","Invalid credentials"));
        } catch (Exception ex) {
            log.error("Login processing error", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error","Internal server error"));
        }
    }
}


