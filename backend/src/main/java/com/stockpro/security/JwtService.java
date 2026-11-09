package com.stockpro.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${stockpro.security.jwt.secret}")
    private String secretKey;

    @Value("${stockpro.security.jwt.access-token-expiration-ms:900000}")
    private long accessTokenExpirationMs; // 15 minutos por defecto

    @Value("${stockpro.security.jwt.refresh-token-expiration-ms:604800000}")
    private long refreshTokenExpirationMs; // 7 días por defecto

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateAccessToken(UUID usuarioId, String correo, UUID negocioId, String rol) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", usuarioId.toString());
        claims.put("negocioId", negocioId != null ? negocioId.toString() : null);
        claims.put("rol", rol);
        claims.put("type", "ACCESS");

        return Jwts.builder()
                .claims(claims)
                .subject(correo)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + accessTokenExpirationMs))
                .signWith(getSigningKey())
                .compact();
    }

    public String generateRefreshToken(UUID usuarioId, String correo) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", usuarioId.toString());
        claims.put("type", "REFRESH");

        return Jwts.builder()
                .claims(claims)
                .subject(correo)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + refreshTokenExpirationMs))
                .signWith(getSigningKey())
                .compact();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public UUID extractUserId(String token) {
        String idStr = extractClaim(token, claims -> claims.get("userId", String.class));
        return idStr != null ? UUID.fromString(idStr) : null;
    }

    public UUID extractNegocioId(String token) {
        String idStr = extractClaim(token, claims -> claims.get("negocioId", String.class));
        return idStr != null ? UUID.fromString(idStr) : null;
    }

    public String extractTokenType(String token) {
        return extractClaim(token, claims -> claims.get("type", String.class));
    }

    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("rol", String.class));
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean isTokenValid(String token, String username) {
        try {
            final String extracted = extractUsername(token);
            return (extracted.equalsIgnoreCase(username)) && !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public long getAccessTokenExpirationMs() {
        return accessTokenExpirationMs;
    }
}
