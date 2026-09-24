package com.utkarsh.jobtracker.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {
    @Value("${jwt,secret}")
    private String secret;

    @Value ("${jwt.expiration-ms:86400000}")
    private long expirationMs;

    private SecretKey getSingingKey() {

     return Keys.hmacShaKey(secret.getBytes());

    }
private String generateToken(String userId){
        Date now = new Date();
        Date expiry =new Date(now.getTime() + expirationMs);
        return Jwts.builder()
                .subject(userId)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(getSingingKey())
                .compact();


}

public String extractUserId(String token) {
    try {
        return extractAllClaims(token).getSubject();
    } catch (Exception e) {
        return null;

    }
}
public boolean isTokenValid(String token) {
        try{
            Claims claims= extarctAllClaims(token);
            return claims.getExpiration().after(new Date());
        }catch (Exception e){
            return false;
        }
}

    private Claims extarctAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSingingKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();


    }
}


