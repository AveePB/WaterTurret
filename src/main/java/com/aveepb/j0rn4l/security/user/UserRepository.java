package com.aveepb.j0rn4l.security.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    //READ:
    Optional<User> findByUsernameAndPassword(String username, String password);
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
}
