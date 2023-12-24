package dev.aveepb.diary.security.service;

import dev.aveepb.diary.security.db.model.User;
import dev.aveepb.diary.security.db.repo.UserRepository;

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
     * @param password the user password.
     * @return the user object.
     */
    public Optional<User> fetchUserByUsernameAndPassword(String username, String password) {

        return this.repository.findByUsernameAndPassword(username, password);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOptional = this.repository.findByUsername(username);

        if (userOptional.isEmpty())
            throw new UsernameNotFoundException("User not found!");

        return userOptional.get();
    }
}
