package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.constants.HeaderConstants;
import ru.practicum.shareit.item.dto.*;

import java.util.Collection;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemClient itemClient;

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDtoResponse> findById(@PathVariable("itemId") int itemId,
                                          @RequestHeader(HeaderConstants.SHARER_USER_ID) int userId) {
        return itemClient.findById(itemId,userId);
    }

    @GetMapping
    public ResponseEntity<Collection<ItemDtoResponse>> findUserItems(@RequestHeader(HeaderConstants.SHARER_USER_ID) int userId) {
        return itemClient.findUserItems(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Collection<ItemDtoResponse>> searchByName(@RequestParam String text,
                                              @RequestHeader(HeaderConstants.SHARER_USER_ID) int userId) {
        return itemClient.searchByName(text,userId);
    }

    @PostMapping
    public ResponseEntity<ItemDtoResponse> saveItem(@Valid @RequestBody ItemDtoRequest itemDtoRequest,
                                           @RequestHeader(HeaderConstants.SHARER_USER_ID) int userId) {
        return itemClient.saveItem(itemDtoRequest,userId);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> deleteItem(@PathVariable("itemId") int itemId,
                                             @RequestHeader(HeaderConstants.SHARER_USER_ID) int userId) {
        return itemClient.deleteItem(itemId,userId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDtoResponse> updateItem(@RequestBody ItemPatchDto itemPatchDto,
                                             @RequestHeader(HeaderConstants.SHARER_USER_ID) int userId,
                                             @PathVariable("itemId") int itemId) {
        return itemClient.updateItem(itemPatchDto,userId,itemId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CommentResponse> saveComment(@RequestBody CommentRequest itemComment,
                                              @RequestHeader(HeaderConstants.SHARER_USER_ID) int userId,
                                              @PathVariable("itemId") int itemId) {
        return itemClient.addComment(itemComment,userId,itemId);
    }

}
