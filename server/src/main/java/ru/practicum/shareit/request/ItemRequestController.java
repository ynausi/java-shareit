package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.constants.HeaderConstants;
import ru.practicum.shareit.request.dto.ItemRequestReq;
import ru.practicum.shareit.request.dto.ItemRequestResponse;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.net.URI;
import java.util.Collection;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @GetMapping
    public ResponseEntity<Collection<ItemRequestResponse>> findUserRequests(
            @RequestHeader(HeaderConstants.SHARER_USER_ID) int userId) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(itemRequestService.findUserRequests(userId));
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<ItemRequestResponse> findItemRequestById(
            @RequestHeader(HeaderConstants.SHARER_USER_ID) int userId,
            @PathVariable("requestId") int requestId) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(itemRequestService.findByRequestId(requestId,userId));
    }

    @PostMapping
    public ResponseEntity<ItemRequestResponse> createItemRequest(@RequestBody ItemRequestReq itemRequestReq,
                                                                @RequestHeader(HeaderConstants.SHARER_USER_ID) int userId) {
        ItemRequestResponse created = itemRequestService.save(itemRequestReq,userId);
        return ResponseEntity.created(URI.create("/" + created.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .body(created);
    }
}
