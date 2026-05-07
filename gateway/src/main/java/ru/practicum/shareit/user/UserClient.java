package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.user.dto.UserPatchRequest;
import ru.practicum.shareit.user.dto.UserRequest;
import ru.practicum.shareit.user.dto.UserResponse;

import java.util.Collection;

@Component
public class UserClient extends BaseClient {

    public UserClient(RestTemplate restTemplate,
                      @Value("${shareit-server.url}") String serverUrl) {
        super(restTemplate, serverUrl);
    }

    public ResponseEntity<Collection<UserResponse>> findAll() {
        return get("/users", null, new ParameterizedTypeReference<Collection<UserResponse>>() {});
    }

    public ResponseEntity<UserResponse> findById(Integer userId) {
        return get("/users/" + userId,null,UserResponse.class);
    }

    public ResponseEntity<UserResponse> saveUser(UserRequest userRequest) {
        return post("/users",null,userRequest, UserResponse.class);
    }

    public ResponseEntity<UserResponse> updateUser(Integer userId, UserPatchRequest userPatchRequest) {
        return patch("/users/" + userId,null,userPatchRequest, UserResponse.class);
    }

    public ResponseEntity<Void> deleteUser(Integer userId) {
        return delete("/users/" + userId,null, Void.class);
    }
}
