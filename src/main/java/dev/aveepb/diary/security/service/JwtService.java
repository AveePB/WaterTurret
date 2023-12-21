package dev.aveepb.diary.security.service;

import dev.aveepb.diary.security.util.JsonWebToken;
import dev.aveepb.diary.security.util.JsonWebTokenClaims;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Optional;

@Service
public class JwtService {

    private static final String SIGNING_KEY = "";

    /**
     * @param token the json web token.
     * @param user the user details.
     * @return true if token is valid otherwise false.
     */
    public boolean isTokenValid(String token, UserDetails user) {

        return JsonWebToken.isTokenValid(token, fetchSigningKey(), user);
    }

    /**
     * @param token the json web token.
     * @return the username.
     */
    public Optional<String> fetchUsername(String token) {

        return Optional.of(JsonWebTokenClaims.extractUsername(token, fetchSigningKey()));
    }

    /**
     * @param token the json web token.
     * @return the user password.
     */
    public Optional<String> fetchUserPassword(String token) {

        return Optional.of(JsonWebTokenClaims.extractPassword(token, fetchSigningKey()));
    }

    /**
     * @return SIGNING_KEY in the form of Key object.
     */
    public Key fetchSigningKey() {
        byte[] decodedKey = Decoders.BASE64.decode(SIGNING_KEY);

        return Keys.hmacShaKeyFor(decodedKey);
    }
}
