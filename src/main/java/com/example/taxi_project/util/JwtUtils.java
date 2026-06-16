package com.example.taxi_project.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtils {




    public String generateToken(String username) {
        long expirationTime = 1000L * 60 * 60 * 24 * 3;
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                 .compact();
    }

    public String generateRefreshToken(String username) {
        long refreshTokenExpirationTime = 1000L * 60 * 60 * 24 * 30; // 30 kun
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpirationTime))
                 .compact();
    }

    public String getUsernameFromToken(String token) {
        return getClaims(token).getSubject();
    }

    public boolean validateToken(String token) {
        try {
            return getClaims(token).getExpiration().after(new Date());
        } catch (Exception e) {
            System.out.println("Token invalid: " + e.getMessage());
            return false;
        }
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaims(token).getExpiration();
    }

    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                 .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
