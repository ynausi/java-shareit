package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exceptions.InternalServerException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserPatchRequest;
import ru.practicum.shareit.user.dto.UserRequest;
import ru.practicum.shareit.user.dto.UserResponse;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void shouldFindAllUsers() {
        User user = user(1);
        UserResponse response = response(1);

        when(userRepository.findAll()).thenReturn(List.of(user));
        when(userMapper.toResponse(user)).thenReturn(response);

        Collection<UserResponse> result = userService.findAll();

        assertEquals(1, result.size());
    }

    @Test
    void shouldFindUserById() {
        int userId = 1;

        User user = user(userId);
        UserResponse response = response(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.toResponse(user)).thenReturn(response);

        UserResponse actual = userService.findById(userId);

        assertSame(response, actual);
    }

    @Test
    void shouldThrowWhenUserNotFoundById() {
        int userId = 1;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.findById(userId));
    }

    @Test
    void shouldSaveUser() {
        UserRequest request = new UserRequest();
        request.setName("John");
        request.setEmail("john@mail.com");

        User user = user(1);
        UserResponse response = response(1);

        when(userMapper.toData(request)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(response);

        UserResponse actual = userService.save(request);

        assertSame(response, actual);
    }

    @Test
    void shouldUpdateUser() {
        int userId = 1;

        User user = user(userId);

        UserPatchRequest patch = new UserPatchRequest();
        patch.setName("Updated");
        patch.setEmail("updated@mail.com");

        UserResponse response = response(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(response);

        UserResponse actual = userService.update(patch, userId);

        assertSame(response, actual);
        assertEquals("Updated", user.getName());
        assertEquals("updated@mail.com", user.getEmail());
    }

    @Test
    void shouldNotUpdateBlankOrNullFields() {
        int userId = 1;

        User user = user(userId);

        UserPatchRequest patch = new UserPatchRequest();
        patch.setName("");
        patch.setEmail(null);

        UserResponse response = response(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(response);

        userService.update(patch, userId);

        assertEquals("User 1", user.getName());
        assertEquals("user1@mail.com", user.getEmail());
    }

    @Test
    void shouldThrowWhenUserNotFoundOnUpdate() {
        int userId = 1;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.update(new UserPatchRequest(), userId));
    }

    @Test
    void shouldDeleteUser() {
        int userId = 1;
        User user = user(userId);

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user))
                .thenReturn(Optional.empty());

        userService.delete(userId);

        verify(userRepository).deleteById(userId);
    }

    @Test
    void shouldThrowWhenUserNotFoundOnDelete() {
        int userId = 1;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.delete(userId));

        verify(userRepository, never()).deleteById(anyInt());
    }

    @Test
    void shouldThrowWhenUserWasNotDeleted() {
        int userId = 1;
        User user = user(userId);

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user))
                .thenReturn(Optional.of(user));

        assertThrows(InternalServerException.class, () -> userService.delete(userId));
    }

    private User user(int id) {
        User user = new User();
        user.setId(id);
        user.setName("User " + id);
        user.setEmail("user" + id + "@mail.com");
        return user;
    }

    private UserResponse response(int id) {
        UserResponse response = new UserResponse();
        response.setId(id);
        response.setName("User " + id);
        response.setEmail("user" + id + "@mail.com");
        return response;
    }
}