package dev.security.diary.service;

import dev.security.diary.controller.request.AuthenticationRequest;
import dev.security.diary.controller.request.RegisterRequest;
import dev.security.diary.controller.response.AuthenticationResponse;
import dev.security.diary.model.User;
import dev.security.diary.repo.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;


    public AuthenticationResponse register(RegisterRequest request) {
        User user = User.builder()
                .email(request.getEmail())
                .nickname(request.getNickname())
                .password(this.passwordEncoder.encode(request.getPassword()))
                .build();

        this.userRepository.save(user);

        String jwt = this.jwtService.generateToken(user);

        return AuthenticationResponse.builder()
                .token(jwt)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        Optional<User> userOptional = this.userRepository.findByEmail(request.getEmail());

        if (userOptional.isEmpty())
            throw new UsernameNotFoundException("User email not found!");

        String jwt = this.jwtService.generateToken(userOptional.get());

        return AuthenticationResponse.builder()
                .token(jwt)
                .build();
    }

}
