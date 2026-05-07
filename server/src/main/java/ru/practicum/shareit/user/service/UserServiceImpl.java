package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import ru.practicum.shareit.exceptions.InternalServerException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserPatchRequest;
import ru.practicum.shareit.user.dto.UserRequest;
import ru.practicum.shareit.user.dto.UserResponse;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public Collection<UserResponse> findAll() {
        return userRepository.findAll().stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponse findById(int userId) {
        return userRepository.findById(userId).map(userMapper::toResponse)
                .orElseThrow(() -> new NotFoundException("No user with id:" + userId)
                );
    }

    @Override
    public UserResponse save(UserRequest userRequest) {
        User user = userMapper.toData(userRequest);
        User created = userRepository.save(user);
        return userMapper.toResponse(created);
    }

    @Override
    public UserResponse update(UserPatchRequest userPatchRequest, int userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("No user with id:" + userId));
        if (userPatchRequest.getEmail() != null && !userPatchRequest.getEmail().isBlank()) {
            user.setEmail(userPatchRequest.getEmail());
        }
        if (userPatchRequest.getName() != null && !userPatchRequest.getName().isBlank()) {
            user.setName(userPatchRequest.getName());
        }
        user = userRepository.save(user);
        return userMapper.toResponse(user);
    }

    @Override
    public void delete(int id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new NotFoundException("No user with id:" + id));
        userRepository.deleteById(id);
        if (userRepository.findById(id).isPresent()) {
            throw new InternalServerException("Не удалось удалить пользователя");
        }
    }
}
