package ru.practicum.shareit.request.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestReq;
import ru.practicum.shareit.request.dto.ItemRequestResponse;
import ru.practicum.shareit.request.model.ItemRequest;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ItemRequestMapper {
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;

    public ItemRequest toData(ItemRequestReq itemRequestReq) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setCreatedTime(LocalDateTime.now());
        itemRequest.setDescription(itemRequestReq.getDescription());
        return itemRequest;
    }

    public ItemRequestResponse toResponse(ItemRequest itemRequest) {
        ItemRequestResponse itemRequestResponse = new ItemRequestResponse();
        itemRequestResponse.setCreated(itemRequest.getCreatedTime());
        itemRequestResponse.setDescription(itemRequest.getDescription());
        itemRequestResponse.setId(itemRequest.getId());
        itemRequestResponse.setItems(itemRepository
                .findAllByItemRequest_Id(itemRequest.getId())
                .stream()
                .map(itemMapper::toItemRequestDtoForResponse)
                .collect(Collectors.toList())
        );
        return itemRequestResponse;
    }
}
