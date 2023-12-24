package com.aveepb.j0rn4l.security.user;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository repository;

    /**
     * @param username the username.
     * @return true if contains otherwise false.
     */
    public boolean contains(String username) {

        return repository.existsByUsername(username);
    }

    /**
     * @param username the username.
     * @param password the user password.
     * @return the user details.
     * @throws UsernameNotFoundException
     */
    public UserDetails loadUserByUsernameAndPassword(String username, String password) throws UsernameNotFoundException {
        Optional<User> userOptional = this.repository.findByUsernameAndPassword(username, password);

        if (userOptional.isEmpty())
            throw new UsernameNotFoundException("User not found");

        return userOptional.get();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOptional = this.repository.findByUsername(username);

        if (userOptional.isEmpty())
            throw new UsernameNotFoundException("User not found!!");

        return userOptional.get();
    }
}
