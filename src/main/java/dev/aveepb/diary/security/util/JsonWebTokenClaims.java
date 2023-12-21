package dev.aveepb.diary.security.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import java.security.Key;
import java.util.Date;

public class JsonWebTokenClaims {

    /**
     * @param token the json web token.
     * @param key the signing key.
     * @return the claim.
     */
    public static String extractUsername(String token, Key key) {
        Claims claims = extractAllClaims(token, key);

        return claims.getSubject();
    }

    /**
     * @param token the json web token.
     * @param key the signing key.
     * @return the claim.
     */
    public static String extractPassword(String token, Key key) {
        Claims claims = extractAllClaims(token, key);

        return claims.get("pass", String.class);
    }

    /**
     * @param token the json web token.
     * @param key the signing key.
     * @return the claim.
     */
    public static Date extractExpirationDate(String token, Key key) {
        Claims claims = extractAllClaims(token, key);

        return claims.getExpiration();
    }

    /**
     * @param token the json web token.
     * @param key the signing key.
     * @return the claims.
     */
    public static Claims extractAllClaims(String token, Key key) {

        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
