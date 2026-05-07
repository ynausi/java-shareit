package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestReq;
import ru.practicum.shareit.request.dto.ItemRequestResponse;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestService itemRequestService;

    @Test
    void shouldCreateItemRequest() throws Exception {
        int userId = 1;
        int requestId = 10;

        ItemRequestReq request = new ItemRequestReq();
        request.setDescription("Хочу дрель");

        ItemRequestResponse response = new ItemRequestResponse();
        response.setId(requestId);
        response.setDescription("Хочу дрель");
        response.setCreated(LocalDateTime.now());

        Mockito.when(itemRequestService.save(
                Mockito.any(ItemRequestReq.class),
                Mockito.eq(userId)
        )).thenReturn(response);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(requestId))
                .andExpect(jsonPath("$.description").value("Хочу дрель"))
                .andExpect(jsonPath("$.created").exists());

        Mockito.verify(itemRequestService).save(
                Mockito.any(ItemRequestReq.class),
                Mockito.eq(userId)
        );
    }

    @Test
    void shouldFindUserRequests() throws Exception {
        int userId = 1;

        ItemRequestResponse response1 = new ItemRequestResponse();
        response1.setId(10);
        response1.setDescription("Хочу дрель");
        response1.setCreated(LocalDateTime.now());

        ItemRequestResponse response2 = new ItemRequestResponse();
        response2.setId(11);
        response2.setDescription("Хочу перфоратор");
        response2.setCreated(LocalDateTime.now());

        Collection<ItemRequestResponse> responses = List.of(response1, response2);

        Mockito.when(itemRequestService.findUserRequests(userId))
                .thenReturn(responses);

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(10))
                .andExpect(jsonPath("$[0].description").value("Хочу дрель"))
                .andExpect(jsonPath("$[1].id").value(11))
                .andExpect(jsonPath("$[1].description").value("Хочу перфоратор"));

        Mockito.verify(itemRequestService).findUserRequests(userId);
    }

    @Test
    void shouldFindItemRequestById() throws Exception {
        int userId = 1;
        int requestId = 10;

        ItemRequestResponse response = new ItemRequestResponse();
        response.setId(requestId);
        response.setDescription("Хочу дрель");
        response.setCreated(LocalDateTime.now());

        Mockito.when(itemRequestService.findByRequestId(requestId, userId))
                .thenReturn(response);

        mockMvc.perform(get("/requests/{requestId}", requestId)
                        .header("X-Sharer-User-Id", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(requestId))
                .andExpect(jsonPath("$.description").value("Хочу дрель"))
                .andExpect(jsonPath("$.created").exists());

        Mockito.verify(itemRequestService).findByRequestId(requestId, userId);
    }
}