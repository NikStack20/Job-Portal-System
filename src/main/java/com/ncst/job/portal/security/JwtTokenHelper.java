package com.ncst.job.portal.security;
import java.util.Date;  
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;

@Component
public class JwtTokenHelper {

    @Value("${app.jwtSecret}")
    private String jwtSecret;

    @Value("${app.jwtExpirationMs:86400000}")
    private long jwtExpirationMs;

    private Key getSigningKey() {
        if (jwtSecret != null && !jwtSecret.isBlank()) {
            try {
                byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
                if (keyBytes.length >= 64) {
                    return Keys.hmacShaKeyFor(keyBytes);
                } 
                // fallthrough to generate
            } catch (Exception ignored) {}
        }
        // generate ephemeral key for dev only
        Key k = Keys.secretKeyFor(SignatureAlgorithm.HS512);
        // WARNING: ephemeral key -> tokens will stop working after restart
        System.err.println("WARNING: No valid app.jwtSecret found — using generated ephemeral key (dev only). " +
                           "Generate & set app.jwtSecret to persist tokens.");
        return k;
    }


    public String generateToken(UserDetails userDetails) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .setSubject(userDetails.getUsername())   // must match loadUserByUsername param
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    public String getUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validate(String token, UserDetails userDetails) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey()).build()
                    .parseClaimsJws(token).getBody();
            return claims.getSubject().equals(userDetails.getUsername()) && !claims.getExpiration().before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}





