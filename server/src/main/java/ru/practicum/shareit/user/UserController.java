package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserPatchRequest;
import ru.practicum.shareit.user.dto.UserRequest;
import ru.practicum.shareit.user.dto.UserResponse;
import ru.practicum.shareit.user.service.UserService;

import java.net.URI;
import java.util.Collection;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<Collection<UserResponse>> findAll() {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(userService.findAll());
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> findById(@PathVariable("userId") int id) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(userService.findById(id));
    }

    @PostMapping
    public ResponseEntity<UserResponse> saveUser(@RequestBody UserRequest userRequest) {
        UserResponse created = userService.save(userRequest);
        return ResponseEntity.created(URI.create("/" + created.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .body(created);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<UserResponse> update(@PathVariable("userId") int id,
                                               @RequestBody UserPatchRequest userPatchRequest) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(userService.update(userPatchRequest,id));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable("userId") int id) {
        return ResponseEntity.ok().build();
    }
}
