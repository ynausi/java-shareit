package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserPatchRequest;
import ru.practicum.shareit.user.dto.UserRequest;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> findAll() {
        return userClient.findAll();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> findById(@PathVariable("userId") Integer userId) {
        return userClient.findById(userId);
    }

    @PostMapping
    public ResponseEntity<Object> saveUser(@Valid @RequestBody UserRequest userRequest) {
        return userClient.saveUser(userRequest);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> update(@PathVariable("userId") Integer userId,
                                         @RequestBody UserPatchRequest userPatchRequest) {
        return userClient.updateUser(userId,userPatchRequest);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable("userId") Integer userId) {
        return userClient.deleteUser(userId);
    }
}
