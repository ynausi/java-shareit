package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.constants.HeaderConstants;
import ru.practicum.shareit.request.dto.ItemRequestReq;
import ru.practicum.shareit.request.dto.ItemRequestResponse;

import java.util.Collection;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @GetMapping
    public ResponseEntity<Collection<ItemRequestResponse>> findUserRequests(
            @RequestHeader(HeaderConstants.SHARER_USER_ID) int userId) {
        return itemRequestClient.findUserRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<ItemRequestResponse> findItemRequestById(
            @RequestHeader(HeaderConstants.SHARER_USER_ID) int userId,
            @PathVariable("requestId") int requestId) {
        return itemRequestClient.findItemRequestById(userId,requestId);
    }

    @PostMapping
    public ResponseEntity<ItemRequestResponse> createItemRequest(@Valid @RequestBody ItemRequestReq itemRequestReq,
                                                    @RequestHeader(HeaderConstants.SHARER_USER_ID) int userId) {
        return itemRequestClient.createItemRequest(itemRequestReq,userId);
    }
}
