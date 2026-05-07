package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserPatchRequest;
import ru.practicum.shareit.user.dto.UserRequest;
import ru.practicum.shareit.user.dto.UserResponse;

import java.util.Collection;

public interface UserService {

    Collection<UserResponse> findAll();

    UserResponse findById(int userId);

    UserResponse save(UserRequest userRequest);

    UserResponse update(UserPatchRequest userPatchRequest,int userId);

    void delete(int id);

}
