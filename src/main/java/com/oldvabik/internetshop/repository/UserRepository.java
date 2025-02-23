package com.oldvabik.internetshop.repository;

import com.oldvabik.internetshop.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByFirstName(String email);
    Optional<User> findByEmail(String email);
}
