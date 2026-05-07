package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.user.dto.UserPatchRequest;
import ru.practicum.shareit.user.dto.UserRequest;

@Component
public class UserClient extends BaseClient {

    public UserClient(RestTemplate restTemplate,
                      @Value("${shareit-server.url}") String serverUrl
    ) {
        super(restTemplate, serverUrl);
    }

    public ResponseEntity<Object> findAll() {
        return get("/users",null);
    }

    public ResponseEntity<Object> findById(Integer userId) {
        return get("/users/" + userId,null);
    }

    public ResponseEntity<Object> saveUser(UserRequest userRequest) {
        return post("/users",null,userRequest);
    }

    public ResponseEntity<Object> updateUser(Integer userId, UserPatchRequest userPatchRequest) {
        return patch("/users/" + userId,null,userPatchRequest);
    }

    public ResponseEntity<Object> deleteUser(Integer userId) {
        return delete("/users/" + userId,null);
    }
}
