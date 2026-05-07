package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.constants.HeaderConstants;
import ru.practicum.shareit.item.dto.*;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemClient itemClient;

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> findById(@PathVariable("itemId") int itemId,
                                          @RequestHeader(HeaderConstants.SHARER_USER_ID) int userId
    ) {
        return itemClient.findById(itemId,userId);
    }

    @GetMapping
    public ResponseEntity<Object> findUserItems(@RequestHeader(HeaderConstants.SHARER_USER_ID) int userId) {
        return itemClient.findUserItems(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchByName(@RequestParam String text,
                                              @RequestHeader(HeaderConstants.SHARER_USER_ID) int userId) {
        return itemClient.searchByName(text,userId);
    }

    @PostMapping
    public ResponseEntity<Object> saveItem(@Valid @RequestBody ItemDtoRequest itemDtoRequest,
                                           @RequestHeader(HeaderConstants.SHARER_USER_ID) int userId) {
        return itemClient.saveItem(itemDtoRequest,userId);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Object> deleteItem(@PathVariable("itemId") int itemId,
                                             @RequestHeader(HeaderConstants.SHARER_USER_ID) int userId) {
        return itemClient.deleteItem(itemId,userId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestBody ItemPatchDto itemPatchDto,
                                             @RequestHeader(HeaderConstants.SHARER_USER_ID) int userId,
                                             @PathVariable("itemId") int itemId) {
        return itemClient.updateItem(itemPatchDto,userId,itemId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> saveComment(@RequestBody CommentRequest itemComment,
                                              @RequestHeader(HeaderConstants.SHARER_USER_ID) int userId,
                                              @PathVariable("itemId") int itemId) {
        return itemClient.addComment(itemComment,userId,itemId);
    }

}
