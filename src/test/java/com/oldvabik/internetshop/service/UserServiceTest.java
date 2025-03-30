package com.oldvabik.internetshop.service;

import com.oldvabik.internetshop.cache.UserCache;
import com.oldvabik.internetshop.dto.UserDto;
import com.oldvabik.internetshop.exception.AlreadyExistsException;
import com.oldvabik.internetshop.exception.ResourceNotFoundException;
import com.oldvabik.internetshop.mapper.UserMapper;
import com.oldvabik.internetshop.model.User;
import com.oldvabik.internetshop.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private UserCache userCache;

    @Test
    void createUser_shouldReturnUser_whenEmailDoesNotExist() {
        UserDto userDto = new UserDto();
        userDto.setFirstName("John");
        userDto.setLastName("Doe");
        userDto.setEmail("john@doe.com");
        userDto.setAge(30);

        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@example.com");
        user.setAge(30);

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setFirstName("John");
        savedUser.setLastName("Doe");
        savedUser.setEmail("john.doe@example.com");
        savedUser.setAge(30);

        Mockito.when(userRepository.existsByEmail(userDto.getEmail())).thenReturn(false);
        Mockito.when(userMapper.toEntity(userDto)).thenReturn(user);
        Mockito.when(userRepository.save(user)).thenReturn(savedUser);
        Mockito.doNothing().when(userCache).put(savedUser.getId(), savedUser);

        User result = userService.createUser(userDto);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(savedUser.getId(), result.getId());
        Mockito.verify(userRepository, Mockito.times(1)).existsByEmail(userDto.getEmail());
        Mockito.verify(userRepository, Mockito.times(1)).save(user);
        Mockito.verify(userCache, Mockito.times(1)).put(savedUser.getId(), savedUser);
    }

    @Test
    void createUser_throwsException_whenEmailDoesExist() {
        UserDto userDto = new UserDto();
        userDto.setFirstName("John");
        userDto.setLastName("Doe");
        userDto.setEmail("john.doe@example.com");
        userDto.setAge(30);

        Mockito.when(userRepository.existsByEmail(userDto.getEmail())).thenReturn(true);

        AlreadyExistsException exception = Assertions.assertThrows(AlreadyExistsException.class, () -> userService.createUser(userDto));
        Assertions.assertTrue(exception.getMessage().contains(userDto.getEmail()));
        Mockito.verify(userRepository, Mockito.times(1)).existsByEmail(userDto.getEmail());
        Mockito.verify(userMapper, Mockito.never()).toEntity(Mockito.any());
    }

    @Test
    void getUsers_shouldReturnUsersAndCacheMissingUsers() {
        User userOne = new User();
        userOne.setId(1L);
        userOne.setFirstName("Alice");
        userOne.setLastName("Smith");
        userOne.setEmail("alice@example.com");
        userOne.setAge(25);

        User userTwo = new User();
        userTwo.setId(2L);
        userTwo.setFirstName("Bob");
        userTwo.setLastName("Brown");
        userTwo.setEmail("bob@example.com");
        userTwo.setAge(28);

        List<User> userList = Arrays.asList(userOne, userTwo);
        Mockito.when(userRepository.findAll()).thenReturn(userList);

        Mockito.when(userCache.get(userOne.getId())).thenReturn(userOne);
        Mockito.when(userCache.get(userTwo.getId())).thenReturn(null);

        List<User> result = userService.getUsers();

        Assertions.assertEquals(2, result.size());
        Mockito.verify(userCache, Mockito.times(1)).put(userTwo.getId(), userTwo);
    }

    @Test
    void getUsers_shouldThrowResourceNotFoundException_whenNoUsers() {
        Mockito.when(userRepository.findAll()).thenReturn(Collections.emptyList());

        ResourceNotFoundException exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> userService.getUsers());
        Assertions.assertTrue(exception.getMessage().contains("Users not found"));
    }

    @Test
    void getUserById_shouldReturnUserFromCache_whenUserExistsInCache() {
        User cachedUser = new User();
        cachedUser.setId(1L);
        cachedUser.setFirstName("Alice");
        cachedUser.setLastName("Smith");
        cachedUser.setEmail("alice@example.com");
        cachedUser.setAge(25);

        Mockito.when(userCache.get(cachedUser.getId())).thenReturn(cachedUser);

        User result = userService.getUserById(cachedUser.getId());

        Assertions.assertEquals(cachedUser, result);
        Mockito.verify(userCache, Mockito.times(1)).get(cachedUser.getId());
        Mockito.verify(userRepository, Mockito.never()).findById(Mockito.any());
    }

    @Test
    void getUserById_shouldReturnUserFromRepository_whenUserNotInCache() {
        User userFromRepo = new User();
        userFromRepo.setId(1L);
        userFromRepo.setFirstName("Alice");
        userFromRepo.setLastName("Smith");
        userFromRepo.setEmail("alice@example.com");
        userFromRepo.setAge(25);

        Mockito.when(userCache.get(userFromRepo.getId())).thenReturn(null);
        Mockito.when(userRepository.findById(userFromRepo.getId())).thenReturn(Optional.of(userFromRepo));

        User result = userService.getUserById(userFromRepo.getId());

        Assertions.assertEquals(userFromRepo, result);
        Mockito.verify(userCache, Mockito.times(1)).get(userFromRepo.getId());
        Mockito.verify(userRepository, Mockito.times(1)).findById(userFromRepo.getId());
        Mockito.verify(userCache, Mockito.times(1)).put(userFromRepo.getId(), userFromRepo);
    }

    @Test
    void getUserById_shouldThrowResourceNotFoundException_whenUserNotFound() {
        Long userId = 1L;
        Mockito.when(userCache.get(userId)).thenReturn(null);
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(userId));
        Assertions.assertTrue(exception.getMessage().contains("User not found"));
    }

    @Test
    void updateUser_shouldUpdateUser_whenValidData() {
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setFirstName("Alice");
        existingUser.setLastName("Smith");
        existingUser.setEmail("alice@example.com");
        existingUser.setAge(25);

        UserDto updateDto = new UserDto();
        updateDto.setFirstName("Alicia");
        updateDto.setLastName("Smithers");
        updateDto.setEmail("alice.new@example.com");
        updateDto.setAge(30);

        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setFirstName("Alicia");
        updatedUser.setLastName("Smithers");
        updatedUser.setEmail("alice.new@example.com");
        updatedUser.setAge(30);

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        // При обновлении email, если такой email отсутствует
        Mockito.when(userRepository.findByEmail(updateDto.getEmail())).thenReturn(Optional.empty());
        // Возвращаем обновленного пользователя при сохранении
        Mockito.when(userRepository.save(existingUser)).thenReturn(updatedUser);

        // Act
        User result = userService.updateUser(1L, updateDto);

        // Assert
        Assertions.assertEquals("Alicia", result.getFirstName());
        Assertions.assertEquals("Smithers", result.getLastName());
        Assertions.assertEquals("alice.new@example.com", result.getEmail());
        Assertions.assertEquals(30, result.getAge());
        Mockito.verify(userRepository, Mockito.times(1)).findById(1L);
        Mockito.verify(userRepository, Mockito.times(1)).findByEmail(updateDto.getEmail());
        Mockito.verify(userRepository, Mockito.times(1)).save(existingUser);
        Mockito.verify(userCache, Mockito.times(1)).put(1L, updatedUser);
    }

    @Test
    void updateUser_shouldThrowResourceNotFoundException_whenUserNotFound() {
        Long userId = 1L;
        UserDto updateDto = new UserDto();
        updateDto.setFirstName("Alicia");
        updateDto.setLastName("Smithers");
        updateDto.setEmail("alice.new@example.com");
        updateDto.setAge(30);

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> userService.updateUser(userId, updateDto));
        Assertions.assertTrue(exception.getMessage().contains("User with id " + userId + " not found"));
    }

    @Test
    void updateUser_shouldThrowAlreadyExistsException_whenEmailAlreadyExists() {
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setFirstName("Alice");
        existingUser.setLastName("Smith");
        existingUser.setEmail("alice@example.com");
        existingUser.setAge(25);

        UserDto updateDto = new UserDto();
        updateDto.setFirstName("Alice");
        updateDto.setLastName("Smith");
        updateDto.setEmail("duplicate@example.com");
        updateDto.setAge(25);

        User anotherUser = new User();
        anotherUser.setId(2L);
        anotherUser.setFirstName("Bob");
        anotherUser.setLastName("Brown");
        anotherUser.setEmail("duplicate@example.com");
        anotherUser.setAge(30);

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        Mockito.when(userRepository.findByEmail(updateDto.getEmail())).thenReturn(Optional.of(anotherUser));

        AlreadyExistsException exception = Assertions.assertThrows(AlreadyExistsException.class, () -> userService.updateUser(1L, updateDto));
        Assertions.assertTrue(exception.getMessage().contains(updateDto.getEmail()));
    }

    @Test
    void deleteUserById_shouldDeleteUser_whenUserExists() {
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setFirstName("Alice");
        existingUser.setLastName("Smith");
        existingUser.setEmail("alice@example.com");
        existingUser.setAge(25);

        Mockito.when(userRepository.findById(existingUser.getId())).thenReturn(Optional.of(existingUser));

        userService.deleteUserById(existingUser.getId());

        Mockito.verify(userRepository, Mockito.times(1)).findById(existingUser.getId());
        Mockito.verify(userRepository, Mockito.times(1)).delete(existingUser);
        Mockito.verify(userCache, Mockito.times(1)).remove(existingUser.getId());
    }

    @Test
    void deleteUserById_shouldThrowResourceNotFoundException_whenUserNotFound() {
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> userService.deleteUserById(1L));
    }

}
