package com.aveepb.j0rn4l.security.jwt;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Optional;

@Service
public class JwtService {

    private static final String SIGNING_KEY = "wy1P/Cl+2sluXkjgtDQrJi2fkLYUicZnJf8WBQN66tKpUAUfK4ds9WLOml/9/phubyOeX5CEzL2Va1hOmunEB2Pp9q4ZnPe8mWrUXXItk3aQJUiMoaZkoqJEkE5orsSmVnadEj3AufaiYUx9v6sLvVLaWZsGy021ek4TlmanIr2Om7jP+mOW57GnOKMI+n3hk/h73C6ImkwPy0M1Nc85aV45leYTHPR1VTZYavLNicMnzLeHerwuxIxf5PHiw7uUadGQTsdy1PTF4WimwDvQp4vPcGJyykxqAXpfBnrO1ud4O2ghaP/aQlshwysV927nKyoERyjp2oNzg0XNNu0M9JZljAXvAC+dqDIt3BQmc20=";

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
     * @return SIGNING_KEY in the form of Key object.
     */
    public Key fetchSigningKey() {
        byte[] decodedKey = Decoders.BASE64.decode(SIGNING_KEY);

        return Keys.hmacShaKeyFor(decodedKey);
    }
}