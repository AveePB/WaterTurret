package dev.aveepb.diary.security.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import org.springframework.security.core.userdetails.UserDetails;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;

public class JsonWebToken {

    /**
     * @param token the json web token.
     * @param key the signing key.
     * @return true if token is expired otherwise false.
     */
    public static boolean isTokenExpired(String token, Key key) {

        return JsonWebTokenClaims.extractExpirationDate(token, key).before(new Date());
    }

    /**
     * @param token the json web token.
     * @param key the signing key.
     * @param user the user details.
     * @return true if token is valid otherwise false.
     */
    public static boolean isTokenValid(String token, Key key, UserDetails user) {
        String username = JsonWebTokenClaims.extractUsername(token, key);

        return (username.equals(user.getUsername()) && !isTokenExpired(token, key));
    }

    /**
     * @param key the signing key.
     * @param user the user details.
     * @return the new token.
     */
    public static String generateToken(Key key, UserDetails user) {

        return generateToken(new HashMap<>(), key, user);
    }

    /**
     * @param extraClaims the hash map storing additional claims.
     * @param key the signing key.
     * @param user the user details.
     * @return the new token.
     */
    public static String generateToken(HashMap<String, Object> extraClaims, Key key, UserDetails user) {
        extraClaims.put("pass", user.getPassword());

        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(user.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000*60))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
}
