package com.oldvabik.internetshop.service;

import com.oldvabik.internetshop.model.User;
import com.oldvabik.internetshop.repository.UserRepository;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userRepository.findAll();
        if (users.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(users);
    }

    public ResponseEntity<User> getUserById(Long id) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(user);
    }

    public ResponseEntity<List<User>> getUsersByFirstNameAndAge(String firstName, Integer age) {
        List<User> users;
        if (firstName != null && age != null) {
            users = userRepository.findByFirstNameAndAge(firstName, age);
        } else if (firstName != null) {
            users = userRepository.findByFirstName(firstName);
        } else if (age != null) {
            users = userRepository.findByAge(age);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        if (users.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(users);
    }

    public User createUser(User user) {
        Optional<User> optionalUser = userRepository.findByEmail(user.getEmail());
        if (optionalUser.isPresent()) {
            throw new IllegalStateException("User already exists");
        }
        user.setAge(Period.between(user.getDateOfBirth(), LocalDate.now()).getYears());
        return userRepository.save(user);
    }

    public void updateUser(Long id, String firstName, String lastName,
                           String email, LocalDate dateOfBirth) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            throw new IllegalStateException("User not found");
        }
        User user = optionalUser.get();

        if (email != null && !email.equals(user.getEmail())) {
            Optional<User> foundByEmail = userRepository.findByEmail(email);
            if (foundByEmail.isPresent()) {
                throw new IllegalStateException("User already exists");
            }
            user.setEmail(email);
        }

        if (firstName != null && !firstName.equals(user.getFirstName())) {
            user.setFirstName(firstName);
        }

        if (lastName != null && !lastName.equals(user.getLastName())) {
            user.setLastName(lastName);
        }

        if (dateOfBirth != null && !dateOfBirth.equals(user.getDateOfBirth())) {
            user.setDateOfBirth(dateOfBirth);
            user.setAge(Period.between(user.getDateOfBirth(), LocalDate.now()).getYears());
        }

        userRepository.save(user);
    }

    public void deleteUser(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            throw new IllegalStateException("User not found");
        }
        userRepository.deleteById(id);
    }
}
