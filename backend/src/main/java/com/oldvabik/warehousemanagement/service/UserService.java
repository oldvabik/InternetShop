package com.oldvabik.warehousemanagement.service;

import com.oldvabik.warehousemanagement.cache.UserCache;
import com.oldvabik.warehousemanagement.dto.UserDto;
import com.oldvabik.warehousemanagement.exception.AlreadyExistsException;
import com.oldvabik.warehousemanagement.exception.ResourceNotFoundException;
import com.oldvabik.warehousemanagement.mapper.UserMapper;
import com.oldvabik.warehousemanagement.model.User;
import com.oldvabik.warehousemanagement.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
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
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new AlreadyExistsException("User with email " + userDto.getEmail() + " already exists");
        }
        log.info("Creating new user: {} {}", userDto.getFirstName(), userDto.getLastName());
        User user = userMapper.toEntity(userDto);
        User savedUser = userRepository.save(user);
        userCache.put(savedUser.getId(), savedUser);
        log.info("User with id {} created and cached", savedUser.getId());
        return savedUser;
    }

    public List<User> getUsers() {
        List<User> users = userRepository.findAll();
        if (users.isEmpty()) {
            throw new ResourceNotFoundException("Users not found");
        }

        for (User user : users) {
            if (userCache.get(user.getId()) == null) {
                userCache.put(user.getId(), user);
                log.info("User with id {} added to cache", user.getId());
            } else {
                log.info("User with id {} already exists in cache", user.getId());
            }
        }
        return users;
    }

    public User getUserById(Long id) {
        User cachedUser = userCache.get(id);
        if (cachedUser != null) {
            log.info("User with id {} retrieved from cache", id);
            return cachedUser;
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        userCache.put(user.getId(), user);
        log.info("User with id {} retrieved from repository", id);
        return user;
    }

    public User updateUser(Long id, UserDto userDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));

        if (userDto.getEmail() != null && !userDto.getEmail().equals(user.getEmail())) {
            Optional<User> foundUser = userRepository.findByEmail(userDto.getEmail());
            if (foundUser.isPresent()) {
                throw new AlreadyExistsException("User with email " + userDto.getEmail() + " already exists");
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
        log.info("Updating user with id {}", user.getId());
        User savedUser = userRepository.save(user);
        userCache.put(savedUser.getId(), savedUser);
        log.info("User with id {} updated and cached", savedUser.getId());
        return savedUser;
    }

    public void deleteUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));
        log.warn("Deleting user with id {}", user.getId());
        userRepository.delete(user);
        userCache.remove(user.getId());
        log.info("User with id {} deleted from cache", user.getId());
    }

}
