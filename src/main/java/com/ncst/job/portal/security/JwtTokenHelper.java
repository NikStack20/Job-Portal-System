package com.ncst.job.portal.security;

import java.util.Collection; 
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtTokenHelper {
    @Value("${app.jwtSecret:helloMyNameIsNCST}")
    private String jwtSecret;
    @Value("${app.jwtExpirationMs:86400000}")
    private long jwtExpirationMs;

    public String generateToken(UserDetails userDetails) {
        Map<String,Object> claims = new HashMap<>();
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        claims.put("roles", authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis()+jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret).compact();
    }

    public String getUsername(String token){ return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject(); }
    public boolean validate(String token, UserDetails userDetails){ try { Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token); return true;} catch(Exception e){return false;} }
}

