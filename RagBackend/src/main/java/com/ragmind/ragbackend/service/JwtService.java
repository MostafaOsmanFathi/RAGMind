package com.ragmind.ragbackend.service;

import com.ragmind.ragbackend.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.access-token-validity}")
    private long accessTokenValidity;

    @Value("${jwt.refresh-token-validity}")
    private long refreshTokenValidity;


    private String generateToken(Map<String, Object> extraClaims, User user, String tokenType, long tokenValidity) {
        extraClaims.put("email", user.getEmail());
        extraClaims.put("name", user.getName());
        extraClaims.put("id", user.getId());
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(tokenType)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + tokenValidity))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256).compact();
    }

    public String generateRefreshToken(Map<String, Object> extraClaims, User user) {
        return generateToken(extraClaims, user, "refresh", refreshTokenValidity);
    }

    public String generateAccessToken(Map<String, Object> extraClaims, User user) {
        return generateToken(extraClaims, user, "access", accessTokenValidity);
    }

    public String extractEmail(String token) {
        return extractClaim(token, (claims -> claims.get("email", String.class)));
    }

    public boolean isRefreshToken(String token) {
        String tokenType = extractClaim(token, Claims::getSubject);
        return tokenType.equals("refresh");
    }

    public boolean isAccessToken(String token) {
        String tokenType = extractClaim(token, Claims::getSubject);
        return tokenType.equals("access");
    }


    public boolean isTokenExpired(String token) {
        return extractExpiration(token).toInstant().isBefore(new Date().toInstant());
    }

    public boolean isTokenValid(String token) {
        try {
            extractAllClaims(token);
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build().parseClaimsJws(token)
                .getBody();
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
