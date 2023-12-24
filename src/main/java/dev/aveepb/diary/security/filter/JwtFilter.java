package dev.aveepb.diary.security.filter;

import dev.aveepb.diary.security.db.model.User;
import dev.aveepb.diary.security.service.JwtService;
import dev.aveepb.diary.security.service.UserService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private static final String PREFIX = "Bearer ";

    private final JwtService jwtService;
    private final UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        //Validate request form.
        if (authHeader != null && authHeader.startsWith(PREFIX)) {

            //Extract important data from raw request.
            String token = authHeader.substring(PREFIX.length());
            Optional<String> username = this.jwtService.fetchUsername(token);
            Optional<String> password = this.jwtService.fetchUserPassword(token);

            //Check user authentication status.
            if ((username.isPresent() && password.isPresent()) && SecurityContextHolder.getContext().getAuthentication() == null) {
                Optional<User> user = this.userService.fetchUserByUsernameAndPassword(username.get(), password.get());

                //Check user token.
                if (user.isPresent() && this.jwtService.isTokenValid(token, user.get())) {

                    //Create authentication for current request.
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, null, user.get().getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    //Update security context.
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }

        //Run next filter.
        filterChain.doFilter(request, response);
    }
}
