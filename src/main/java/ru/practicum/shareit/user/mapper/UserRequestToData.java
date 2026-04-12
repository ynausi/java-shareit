package ru.practicum.shareit.user.mapper;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.dto.UserRequest;
import ru.practicum.shareit.user.dto.UserResponse;
import ru.practicum.shareit.user.model.User;

@Repository
public class UserRequestToData {
        public User toData(UserRequest userRequest) {
            User user = new User();
            user.setName(userRequest.getName());
            user.setEmail(userRequest.getEmail());
            return user;
        }
}

