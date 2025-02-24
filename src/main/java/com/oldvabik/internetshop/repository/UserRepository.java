package com.oldvabik.internetshop.repository;

import com.oldvabik.internetshop.model.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByFirstNameAndAge(String email, Integer age);

    List<User> findByFirstName(String firstName);

    Optional<User> findByEmail(String email);

    List<User> findByAge(Integer age);
}
