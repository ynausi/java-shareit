package ru.practicum.shareit.item;

import constants.HeaderConstants;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import java.net.URI;
import java.util.Collection;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDtoResponse> findById(@PathVariable("itemId") int id) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(itemService.findById(id));
    }

    @GetMapping
    public ResponseEntity<Collection<ItemDtoResponse>> findUserItems(@RequestHeader(HeaderConstants.SHARER_USER_ID) int userId) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(itemService.findUserItems(userId));
    }

    @GetMapping("/search")
    public ResponseEntity<Collection<ItemDtoResponse>> searchByName(@RequestParam String text) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(itemService.findByName(text));
    }

    @PostMapping
    public ResponseEntity<ItemDtoResponse> saveItem(@Valid @RequestBody ItemDtoRequest itemDtoRequest,
                                                    @RequestHeader(HeaderConstants.SHARER_USER_ID) int userId) {
        ItemDtoResponse created = itemService.save(itemDtoRequest,userId);
        return ResponseEntity.created(URI.create("/" + created.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .body(created);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<?> deleteItem(@PathVariable("itemId") int id) {
        itemService.delete(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDtoResponse> updateItem(@RequestBody ItemPatchDto itemPatchDto,
                                                      @RequestHeader(HeaderConstants.SHARER_USER_ID) int userId,
                                                      @PathVariable("itemId") int itemId) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(itemService.update(itemPatchDto,itemId,userId));
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CommentResponse> saveComment(@RequestBody CommentRequest itemComment,
                                                       @RequestHeader(HeaderConstants.SHARER_USER_ID) int userId,
                                                       @PathVariable("itemId") int itemId) {
        CommentResponse created = itemService.saveComment(itemComment,userId,itemId);
        return ResponseEntity.created(URI.create("/" + created.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .body(created);
    }

}
