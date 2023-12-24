package com.aveepb.j0rn4l.auth;

import com.aveepb.j0rn4l.security.jwt.JsonWebToken;
import com.aveepb.j0rn4l.security.jwt.JwtService;
import com.aveepb.j0rn4l.security.user.Role;
import com.aveepb.j0rn4l.security.user.User;
import com.aveepb.j0rn4l.security.user.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    /**
     * @param request the client's request.
     * @return the json web token.
     * @throws Exception the username taken.
     */
    public String register(AuthRequest request) throws Exception {
        if (this.userRepository.existsByUsername(request.getUsername()))
            throw new Exception("This username is already registered!");

        User user = User.builder()
                .username(request.getUsername())
                .password(this.passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();

        this.userRepository.save(user);
        return JsonWebToken.generateToken(this.jwtService.fetchSigningKey(), user);
    }

    /**
     * @param request the client's request.
     * @return the json web token.
     * @throws Exception the user not found ex.
     */
    public String login(AuthRequest request) throws Exception {
        this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        Optional<User> user = this.userRepository.findByUsername(request.getUsername());

        if (user.isEmpty())
            throw new Exception("User not found!");

        return JsonWebToken.generateToken(this.jwtService.fetchSigningKey(), user.get());
    }
}
