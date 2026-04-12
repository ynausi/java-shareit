package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.InternalServerException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserPatchRequest;
import ru.practicum.shareit.user.dto.UserRequest;
import ru.practicum.shareit.user.dto.UserResponse;
import ru.practicum.shareit.user.mapper.UserDataToUserResponse;
import ru.practicum.shareit.user.mapper.UserRequestToData;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserDataToUserResponse userDataToUserResponse;
    private final UserRequestToData userRequestToData;

    @Override
    public Collection<UserResponse> findAll() {
        return userRepository.findAll().stream()
                .map(userDataToUserResponse::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponse findById(int id) {
        return userRepository.findById(id).map(userDataToUserResponse::toResponse)
                .orElseThrow(() -> new NotFoundException("No user with id:" + id)
                );
    }

    @Override
    public UserResponse save(UserRequest userRequest) {
        User user = userRequestToData.toData(userRequest);
        User created = userRepository.save(user);
        return userDataToUserResponse.toResponse(created);
    }

    @Override
    public UserResponse update(UserPatchRequest userPatchRequest,int id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new NotFoundException("No user with id:" + id));
        if (userPatchRequest.getEmail() != null && !userPatchRequest.getEmail().isBlank()) {
            user.setEmail(userPatchRequest.getEmail());
        }
        if (userPatchRequest.getName() != null && !userPatchRequest.getName().isBlank()) {
            user.setName(userPatchRequest.getName());
        }
        user = userRepository.update(user);
        return userDataToUserResponse.toResponse(user);
    }

    @Override
    public void delete(int id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new NotFoundException("No user with id:" + id));
        userRepository.delete(id);
        if (userRepository.findById(id).isPresent()) {
            throw new InternalServerException("Не удалось удалить пользователя");
        }
    }
}
