package com.ncst.job.portal.controllers;
import java.util.Map;  
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ncst.job.portal.loadouts.LoginRequest;
import com.ncst.job.portal.security.JwtTokenHelper;
import com.ncst.job.portal.service.CustomUserDetailsService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtTokenHelper jwt;
    private final CustomUserDetailsService uds;

    public AuthController(AuthenticationManager authManager, JwtTokenHelper jwt, CustomUserDetailsService uds) {
        this.authManager = authManager; this.jwt = jwt; this.uds = uds;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req) {
        if (req == null || req.getEmail() == null || req.getPassword() == null
            || req.getEmail().isBlank() || req.getPassword().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error","email and password are required"));
        }
        try {
            Authentication a = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword())
            );
            UserDetails ud = (UserDetails) a.getPrincipal();
            String token = jwt.generateToken(ud);
            return ResponseEntity.ok(Map.of("token", token));
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error","Invalid credentials"));
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error","server error"));
        }
    }
}




