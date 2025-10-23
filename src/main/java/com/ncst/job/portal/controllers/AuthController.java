package com.ncst.job.portal.controllers;

import java.util.Arrays;

import javax.naming.AuthenticationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ncst.job.portal.Repository.RoleRepo;
import com.ncst.job.portal.Repository.UserRepo;
import com.ncst.job.portal.entities.Role;
import com.ncst.job.portal.entities.User;
import com.ncst.job.portal.loadouts.UserDto;
import com.ncst.job.portal.security.JwtTokenHelper;

import io.swagger.v3.oas.annotations.parameters.RequestBody;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private UserRepo userRepo;
    
    @Autowired
    private RoleRepo roleRepo;
    
    @Autowired
    private PasswordEncoder encoder;
    
    @Autowired
    private AuthenticationManager authManager; // define bean if needed
    
    @Autowired private JwtTokenHelper jwtUtil;
    
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserDto dto){
        if(userRepo.findByEmail(dto.getEmail()).isPresent()) return ResponseEntity.badRequest().body("Email exists");
        User u=new User(); u.setUsername(dto.getName()); u.setEmail(dto.getEmail()); u.setPassword(encoder.encode(dto.getPassword()));
        Role r = roleRepo.findByName(dto.isEmployer()? "ROLE_EMPLOYER":"ROLE_APPLICANT").orElseThrow();
        u.setRoles(new HashSet<>(Arrays.asList(r)));
        userRepo.save(u);
        return ResponseEntity.ok("Registered");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDto ld){
        try {
            Authentication auth = authManager.authenticate(new UsernamePasswordAuthenticationToken(ld.getEmail(), ld.getPassword()));
            String jwt = jwtUtil.generateToken((UserDetails) auth.getPrincipal());
            return ResponseEntity.ok(Collections.singletonMap("token", jwt));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid");
        }
    }
}

