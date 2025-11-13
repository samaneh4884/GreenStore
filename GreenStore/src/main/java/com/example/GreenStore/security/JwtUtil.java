package com.example.GreenStore.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtUtil {
    private final String SECRET = "Kp9RkB0V5OJRLvTibdsqfp5dVbcXEl6U4ckE2ITWnWQ";

    public String generateToken(String username, String password) {
        return Jwts.builder().setSubject(username).claim("password", password)
                .setIssuedAt(new Date(System.currentTimeMillis())).setExpiration(new Date(
                        System.currentTimeMillis() + 24 * 60 * 60 * 1000)).signWith(SignatureAlgorithm.HS256, SECRET).compact();
    }

    public String extractUsername(String token) {
        return Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateToken(String token, UserDetails user) {
        return !isExpired(token) && extractUsername(token).equals(user.getUsername());
    }

    public boolean isExpired(String token) {
        Date expirationDate = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token).getBody().getExpiration();
        return expirationDate.before(new Date());
    }


}
