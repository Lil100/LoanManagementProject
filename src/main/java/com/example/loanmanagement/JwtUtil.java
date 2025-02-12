package com.example.loanmanagement;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    private static final String SECRET = "mySecretKeyForJwtMySecretKeyForJwt";  // Must be at least 32 characters
    private static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(SECRET.getBytes());  // Convert to a proper key
    private static final long EXPIRATION_TIME = 1000 * 60 * 15;  // 15 minutes

    /**
     * Generates a JWT token for a given username.
     */
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Generates a JWT token for a given UserDetails object.
     */
    public String generateToken(UserDetails userDetails) {
        return generateToken(userDetails.getUsername());
    }

    /**
     * Extracts the username from the JWT token.
     */
    public String extractUsername(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (JwtException e) {
            return null;  // Invalid token
        }
    }

    /**
     * Checks if a JWT token is expired.
     */
    private boolean isTokenExpired(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getExpiration()
                    .before(new Date());
        } catch (JwtException e) {
            return true;  // Assume expired if parsing fails
        }
    }

    /**
     * Validates a JWT token against a given username.
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return username != null && username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }
}
