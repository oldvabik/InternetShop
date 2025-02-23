package com.oldvabik.internetshop.service;

import com.oldvabik.internetshop.model.User;
import com.oldvabik.internetshop.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Optional;


@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public List<User> getUsersByFirstName(String firstName) {
        return userRepository.findByFirstName(firstName);
    }

    public User createUser(User user) {
        Optional<User> optionalUser = userRepository.findByEmail(user.getEmail());
        if (optionalUser.isPresent()) {
            throw new IllegalStateException("User already exists");
        }
        user.setAge(Period.between(user.getDateOfBirth(), LocalDate.now()).getYears());
        return userRepository.save(user);
    }

    public void updateUser(Long id, String firstName, String lastName, String email, LocalDate dateOfBirth) {
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
