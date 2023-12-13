package dev.security.diary.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;


@Service
public class JwtService {
    private static final String SECRET_KEY_256_BIT = "s2UJv4ZcEHCcvnW436I8guoM9ldINaVC0CZprkCyZD78oIDVTh4RXZTaNsSjx8M57oMiL0XTSnFYUxOskY0Tg0KUfOTZPNM3E23kyRT2Oybe31MeVHAmDLBrAXqXb0TSFwSPRnoe0uTyRoVU0bXToJopfX4IyCaOyg5wKyvIfovUguUmcwfd4ChEzSNVuF28cH0C2kvcohLVLkfiEY08c56jH07ZozLMhVx9d0ozIlVVzcCPlWYkTYXNtGAk5bK8";

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY_256_BIT);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
