package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.*;

import java.util.Collection;

@Component
public class ItemClient extends BaseClient {

    public ItemClient(RestTemplate restTemplate,
                      @Value("${shareit-server.url}") String serverUrl) {
        super(restTemplate, serverUrl);
    }

    public ResponseEntity<ItemDtoResponse> findById(Integer itemId, Integer userId) {
        return get("/items/" + itemId,userId,ItemDtoResponse.class);
    }

    public ResponseEntity<Collection<ItemDtoResponse>> findUserItems(Integer userId) {
        return get("/items", userId, new ParameterizedTypeReference<Collection<ItemDtoResponse>>() {});
    }

    public ResponseEntity<Collection<ItemDtoResponse>> searchByName(String text,Integer userId) {
        return get("/items/search?text=" + text,userId,new ParameterizedTypeReference<Collection<ItemDtoResponse>>() {});
    }

    public ResponseEntity<ItemDtoResponse> saveItem(ItemDtoRequest itemDtoRequest,Integer userId) {
        return post("/items",userId,itemDtoRequest,ItemDtoResponse.class);
    }

    public ResponseEntity<Void> deleteItem(Integer itemId,Integer userId) {
        return delete("/items/" + itemId,userId, Void.class);
    }

    public ResponseEntity<ItemDtoResponse> updateItem(ItemPatchDto itemPatchDto,Integer userId,Integer itemId) {
        return patch("/items/" + itemId,userId,itemPatchDto,ItemDtoResponse.class);
    }

    public ResponseEntity<CommentResponse> addComment(CommentRequest commentRequest, Integer userId, Integer itemId) {
        return post("/items/" + itemId + "/comment",userId,commentRequest,CommentResponse.class);
    }
}
