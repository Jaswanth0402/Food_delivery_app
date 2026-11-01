package com.jaswanth.fooddeliveryplatform.security;

import com.jaswanth.fooddeliveryplatform.user.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

@Service
public class JwtService {

    private final SecretKey secretKey;
    private final Long expiration;

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration}") long expiration
    ){
        this.secretKey = Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8)
        );
        this.expiration = expiration;
    }

    public String generateToken(User user){
        Date now = new Date();

        Date expireDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .subject(user.getId().toString())
                .claim("email",user.getEmail())
                .claim("roles",
                        user.getRole()
                                .stream()
                                .map(Enum::name)
                                .toList()
                        )
                .issuedAt(now)
                .expiration(expireDate)
                .signWith(secretKey)
                .compact();
    }
    public String extractUserId(String token){
        return getClaims(token)
                .getSubject();

    }
    public List<String> extractRoles(String token) {

        Object roles = getClaims(token).get("roles");

        if (roles instanceof List<?> roleList) {
            return roleList.stream()
                    .map(Object::toString)
                    .toList();
        }

        return List.of();
    }
    public boolean isTokenValid(String token){
        try {
            getClaims(token);
            return true;
        }
        catch(Exception exception){
            return  false;
        }
    }

    public Claims getClaims(String token){
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
