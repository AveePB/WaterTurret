package dev.aveepb.diary.security.service;

import dev.aveepb.diary.security.db.model.User;
import dev.aveepb.diary.security.db.repo.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;

    /**
     * @param username the username.
     * @param password the user password.
     * @return the user object.
     */
    public Optional<User> fetchUserByUsernameAndPassword(String username, String password) {

        return this.repository.findByUsernameAndPassword(username, password);
    }

}
