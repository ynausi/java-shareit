package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentRequest;
import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.item.dto.ItemPatchDto;

@Component
public class ItemClient extends BaseClient {


    public ItemClient(RestTemplate restTemplate,
                      @Value("${shareit-server.url}") String serverUrl) {
        super(restTemplate, serverUrl);
    }

    public ResponseEntity<Object> findById(Integer itemId,Integer userId) {
        return get("/items/" + itemId,userId);
    }

    public ResponseEntity<Object> findUserItems(Integer userId) {
        return get("/items",userId);
    }

    public ResponseEntity<Object> searchByName(String text,Integer userId) {
        return get("/items/search?text=" + text,userId);
    }

    public ResponseEntity<Object> saveItem(ItemDtoRequest itemDtoRequest,Integer userId) {
        return post("/items",userId,itemDtoRequest);
    }

    public ResponseEntity<Object> deleteItem(Integer itemId,Integer userId) {
        return delete("/items/" + itemId,userId);
    }

    public ResponseEntity<Object> updateItem(ItemPatchDto itemPatchDto,Integer userId,Integer itemId) {
        return patch("/items/" + itemId,userId,itemPatchDto);
    }

    public ResponseEntity<Object> addComment(CommentRequest commentRequest,Integer userId,Integer itemId) {
        return post("/items/" + itemId + "/comment",userId,commentRequest);
    }
}
