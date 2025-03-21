package com.oldvabik.internetshop.service;

import com.oldvabik.internetshop.cache.UserCache;
import com.oldvabik.internetshop.dto.UserDto;
import com.oldvabik.internetshop.mapper.UserMapper;
import com.oldvabik.internetshop.model.User;
import com.oldvabik.internetshop.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserCache userCache;

    public UserService(UserRepository userRepository,
                       UserMapper userMapper,
                       UserCache userCache) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.userCache = userCache;
    }

    public User createUser(UserDto userDto) {
        Optional<User> optionalUser = userRepository.findByEmail(userDto.getEmail());
        if (optionalUser.isPresent()) {
            throw new IllegalStateException("User already exists");
        }
        User user = userMapper.toEntity(userDto);
        user = userRepository.save(user);
        userCache.put(user.getId(), user);
        return user;
    }

    public ResponseEntity<List<User>> getUsers() {
        List<User> users = userRepository.findAll();
        if (users.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(users);
    }

    public ResponseEntity<User> getUserById(Long id) {
        User user = userCache.get(id);
        if (user == null) {
            user = userRepository.findById(id).orElse(null);
            if (user != null) {
                userCache.put(id, user);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        }
        return ResponseEntity.ok(user);
    }

    public User updateUser(Long id, UserDto userDto) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            throw new IllegalStateException("User does not exist");
        }
        User user = optionalUser.get();

        if (userDto.getEmail() != null && !userDto.getEmail().equals(user.getEmail())) {
            Optional<User> foundUser = userRepository.findByEmail(userDto.getEmail());
            if (foundUser.isPresent()) {
                throw new IllegalStateException("User already exists");
            }
            user.setEmail(userDto.getEmail());
        }

        if (userDto.getFirstName() != null && !userDto.getFirstName().equals(user.getFirstName())) {
            user.setFirstName(userDto.getFirstName());
        }

        if (userDto.getLastName() != null && !userDto.getLastName().equals(user.getLastName())) {
            user.setLastName(userDto.getLastName());
        }

        if (userDto.getAge() != null && !userDto.getAge().equals(user.getAge())) {
            user.setAge(userDto.getAge());
        }
        user = userRepository.save(user);
        userCache.put(user.getId(), user);
        return user;
    }

    public void deleteUserById(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            throw new IllegalStateException("User does not exist");
        }
        userRepository.deleteById(id);
        userCache.remove(id);
    }

    public void clearCache() {
        userCache.clear();
    }

}
