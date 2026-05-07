package ru.practicum.shareit;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
public class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    @Test
    void shouldCreateItem() throws Exception {

        Integer userId = 1;

        ItemDtoRequest request = new ItemDtoRequest();
        request.setName("Дрель");
        request.setDescription("Обычная дрель");
        request.setAvailable(true);

        ItemDtoResponse response = new ItemDtoResponse();
        response.setId(10);
        response.setName("Дрель");
        response.setDescription("Обычная дрель");
        response.setAvailable(true);

        Mockito.when(itemService.save(request,userId))
                .thenReturn(response);

        mockMvc.perform(post("/items")
                .header("X-Sharer-User-Id",userId)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.name").value("Дрель"))
                .andExpect(jsonPath("$.description").value("Обычная дрель"))
                .andExpect(jsonPath("$.available").value(true));
        Mockito.verify(itemService).save(request,userId);

    }

    @Test
    void shouldFindItemById() throws Exception {
        Integer userId = 1;
        Integer itemId = 10;

        ItemDtoResponse response = new ItemDtoResponse();
        response.setId(itemId);
        response.setName("Дрель");
        response.setDescription("Обычная дрель");
        response.setAvailable(true);

        Mockito.when(itemService.findById(itemId))
                .thenReturn(response);

        mockMvc.perform(get("/items/{itemId}",itemId)
                        .header("X-Sharer-User-Id",userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.name").value("Дрель"))
                .andExpect(jsonPath("$.description").value("Обычная дрель"))
                .andExpect(jsonPath("$.available").value(true));
        Mockito.verify(itemService).findById(itemId);
    }

    @Test
    void shouldFindCollectionOfItemsForUser() throws Exception {
        Integer userId = 1;
        Integer itemId1 = 10;
        Integer itemId2 = 5;

        ItemDtoResponse response1 = new ItemDtoResponse();
        response1.setId(itemId1);
        response1.setName("Дрель");
        response1.setDescription("Обычная дрель");
        response1.setAvailable(true);

        ItemDtoResponse response2 = new ItemDtoResponse();
        response2.setId(itemId2);
        response2.setName("Перфоратор");
        response2.setDescription("Отличный перфоратор");
        response2.setAvailable(true);

        Collection<ItemDtoResponse> collectionItemResponse = new ArrayList<>();
        collectionItemResponse.add(response1);
        collectionItemResponse.add(response2);

        Mockito.when(itemService.findUserItems(userId)).thenReturn(collectionItemResponse);

        mockMvc.perform(get("/items")
                .header("X-Sharer-User-Id",userId)
                .characterEncoding(StandardCharsets.UTF_8)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(10))
                .andExpect(jsonPath("$[0].name").value("Дрель"))
                .andExpect(jsonPath("$[0].description").value("Обычная дрель"))
                .andExpect(jsonPath("$[0].available").value(true))
                .andExpect(jsonPath("$[1].id").value(5))
                .andExpect(jsonPath("$[1].name").value("Перфоратор"))
                .andExpect(jsonPath("$[1].description").value("Отличный перфоратор"))
                .andExpect(jsonPath("$[1].available").value(true));
        Mockito.verify(itemService).findUserItems(userId);
    }

    @Test
    void shouldFindCollectionByName() throws Exception {
        Integer userId = 1;
        Integer itemId1 = 10;
        Integer itemId2 = 5;

        ItemDtoResponse response1 = new ItemDtoResponse();
        response1.setId(itemId1);
        response1.setName("Дрель");
        response1.setDescription("Обычная дрель");
        response1.setAvailable(true);

        ItemDtoResponse response2 = new ItemDtoResponse();
        response2.setId(itemId2);
        response2.setName("Перфоратор");
        response2.setDescription("Отличный перфоратор");
        response2.setAvailable(true);

        Collection<ItemDtoResponse> collectionItemResponse = new ArrayList<>();
        collectionItemResponse.add(response1);

        Mockito.when(itemService.findByName("Дрель")).thenReturn(collectionItemResponse);

        mockMvc.perform(get("/items/search?text=Дрель")
                        .header("X-Sharer-User-Id",userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(10))
                .andExpect(jsonPath("$[0].name").value("Дрель"))
                .andExpect(jsonPath("$[0].description").value("Обычная дрель"))
                .andExpect(jsonPath("$[0].available").value(true));
        Mockito.verify(itemService).findByName("Дрель");
    }

    @Test
    void shouldUpdateItem() throws Exception {
        Integer userId = 1;
        Integer itemId = 10;

        ItemPatchDto request = new ItemPatchDto();
        request.setName("Обновлённая дрель");
        request.setDescription("Теперь почти новая");
        request.setAvailable(false);

        ItemDtoResponse response = new ItemDtoResponse();
        response.setId(itemId);
        response.setName("Обновлённая дрель");
        response.setDescription("Теперь почти новая");
        response.setAvailable(false);

        Mockito.when(itemService.update(request,itemId,userId))
                .thenReturn(response);

        mockMvc.perform(patch("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemId))
                .andExpect(jsonPath("$.name").value("Обновлённая дрель"))
                .andExpect(jsonPath("$.description").value("Теперь почти новая"))
                .andExpect(jsonPath("$.available").value(false));
        Mockito.verify(itemService).update(request,itemId,userId);
    }

    @Test
    void shouldDeleteItem() throws Exception {
        Integer itemId = 10;

        mockMvc.perform(delete("/items/{itemId}", itemId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Mockito.verify(itemService).delete(itemId);
    }

    @Test
    void shouldAddCommentToItem() throws Exception {
        Integer userId = 1;
        Integer itemId = 10;

        ItemPatchDto request = new ItemPatchDto();
        request.setName("Обновлённая дрель");
        request.setDescription("Теперь почти новая");
        request.setAvailable(true);

        CommentRequest commentRequest = new CommentRequest("Отличная, пользовался");

        ItemDtoResponse response = new ItemDtoResponse();
        response.setId(itemId);
        response.setName("Обновлённая дрель");
        response.setDescription("Теперь почти новая");
        response.setAvailable(true);
        List<CommentResponse> commentResponses = new ArrayList<>();
        CommentResponse commentResponses1 = new CommentResponse(1,"Отличная, пользовался","Ynausi", LocalDateTime.now());
        commentResponses.add(commentResponses1);
        response.setComments(commentResponses);

        Mockito.when(itemService.saveComment(commentRequest,userId,itemId)).thenReturn(commentResponses1);

        mockMvc.perform(post("/items/{itemId}/comment",itemId)
                .header("X-Sharer-User-Id", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(commentRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.text").value("Отличная, пользовался"))
                        .andExpect(jsonPath("$.authorName").value("Ynausi"));

        Mockito.verify(itemService).saveComment(commentRequest,userId,itemId);
    }
}
