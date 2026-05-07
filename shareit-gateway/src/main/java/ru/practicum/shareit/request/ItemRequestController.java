package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.constants.HeaderConstants;
import ru.practicum.shareit.request.dto.ItemRequestReq;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @GetMapping
    public ResponseEntity<Object> findUserRequests(
            @RequestHeader(HeaderConstants.SHARER_USER_ID) int userId) {
        return itemRequestClient.findUserRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> findItemRequestById(
            @RequestHeader(HeaderConstants.SHARER_USER_ID) int userId,
            @PathVariable("requestId") int requestId) {
        return itemRequestClient.findItemRequestById(userId,requestId);
    }

    @PostMapping
    public ResponseEntity<Object> createItemRequest(@Valid @RequestBody ItemRequestReq itemRequestReq,
                                                    @RequestHeader(HeaderConstants.SHARER_USER_ID) int userId) {
        return itemRequestClient.createItemRequest(itemRequestReq,userId);
    }
}
