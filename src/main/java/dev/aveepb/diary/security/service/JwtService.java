package dev.aveepb.diary.security.service;

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
import java.util.function.Function;

@Service
public class JwtService {

    private static final String SIGNING_KEY = "";

    //PRIVATE:

    /**
     * @return SIGNING_KEY in the form of Key object
     */
    private Key getSigningKey() {
        byte[] decodedKey = Decoders.BASE64.decode(SIGNING_KEY);

        return Keys.hmacShaKeyFor(decodedKey);
    }

    /**
     * @param token the json web token.
     * @param func the function parameter.
     * @return the claim.
     * @param <T> the type of function result.
     */
    private <T> T extractClaim(String token, Function<Claims, T> func) {
        Claims claims = extractAllClaims(token);

        return func.apply(claims);
    }

    /**
     * @param token the json web token.
     * @return the claims.
     */
    private Claims extractAllClaims(String token) {

        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * @param token the json web token.
     * @return the expiration date.
     */
    private Date extractExpirationDate(String token) {

        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * @param token the json web token.
     * @return true if token is expired otherwise false.
     */
    private boolean isTokenExpired(String token) {

        return extractExpirationDate(token).before(new Date());
    }

    //PUBLIC:

    /**
     * @param token the json web token.
     * @return the username.
     */
    public String extractUsername(String token) {

        return extractClaim(token, Claims::getSubject);
    }

    /**
     * @param userDetails the user details.
     * @return the new token.
     */
    public String generateToken(UserDetails userDetails) {

        return generateToken(new HashMap<>(), userDetails);
    }

    /**
     * @param extraClaims the hash map storing additional claims.
     * @param userDetails the user details.
     * @return the new token.
     */
    public String generateToken(HashMap<String, Object> extraClaims, UserDetails userDetails) {

        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000*60))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * @param token the json web token.
     * @param userDetails the user details.
     * @return true if token is valid otherwise false.
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        String username = extractUsername(token);

        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

}
