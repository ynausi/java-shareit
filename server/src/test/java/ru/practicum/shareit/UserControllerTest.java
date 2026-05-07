package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.dto.UserPatchRequest;
import ru.practicum.shareit.user.dto.UserRequest;
import ru.practicum.shareit.user.dto.UserResponse;
import ru.practicum.shareit.user.service.UserService;
import java.util.Collection;
import java.util.List;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    void shouldCreateUser() throws Exception {
        int userId = 1;

        UserRequest request = new UserRequest();
        request.setName("Ynausi");
        request.setEmail("trot@gmail.com");

        UserResponse response = new UserResponse();
        response.setId(userId);
        response.setName("Ynausi");
        response.setEmail("trot@gmail.com");

        Mockito.when(userService.save(Mockito.any(UserRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.name").value("Ynausi"))
                .andExpect(jsonPath("$.email").value("trot@gmail.com"));

        Mockito.verify(userService).save(Mockito.any(UserRequest.class));
    }

    @Test
    void shouldFindUserById() throws Exception {
        int userId = 1;

        UserResponse response = new UserResponse();
        response.setId(userId);
        response.setName("Ynausi");
        response.setEmail("trot@gmail.com");

        Mockito.when(userService.findById(userId))
                .thenReturn(response);

        mockMvc.perform(get("/users/{userId}", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.name").value("Ynausi"))
                .andExpect(jsonPath("$.email").value("trot@gmail.com"));

        Mockito.verify(userService).findById(userId);
    }

    @Test
    void shouldFindAllUsers() throws Exception {
        UserResponse response1 = new UserResponse();
        response1.setId(1);
        response1.setName("Ynausi");
        response1.setEmail("trot@gmail.com");

        UserResponse response2 = new UserResponse();
        response2.setId(2);
        response2.setName("John");
        response2.setEmail("john@gmail.com");

        Collection<UserResponse> responses = List.of(response1, response2);

        Mockito.when(userService.findAll())
                .thenReturn(responses);

        mockMvc.perform(get("/users")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Ynausi"))
                .andExpect(jsonPath("$[0].email").value("trot@gmail.com"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("John"))
                .andExpect(jsonPath("$[1].email").value("john@gmail.com"));

        Mockito.verify(userService).findAll();
    }

    @Test
    void shouldUpdateUser() throws Exception {
        int userId = 1;

        UserPatchRequest request = new UserPatchRequest();
        request.setName("Updated");
        request.setEmail("updated@gmail.com");

        UserResponse response = new UserResponse();
        response.setId(userId);
        response.setName("Updated");
        response.setEmail("updated@gmail.com");

        Mockito.when(userService.update(
                Mockito.any(UserPatchRequest.class),
                Mockito.eq(userId)
        )).thenReturn(response);

        mockMvc.perform(patch("/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.name").value("Updated"))
                .andExpect(jsonPath("$.email").value("updated@gmail.com"));

        Mockito.verify(userService).update(
                Mockito.any(UserPatchRequest.class),
                Mockito.eq(userId)
        );
    }

    @Test
    void shouldDeleteUser() throws Exception {
        int userId = 1;

        mockMvc.perform(delete("/users/{userId}", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }
}