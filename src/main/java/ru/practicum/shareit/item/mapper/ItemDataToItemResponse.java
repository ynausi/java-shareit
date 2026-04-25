package ru.practicum.shareit.item.mapper;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.model.Item;

@Repository
public class ItemDataToItemResponse {
    public ItemDtoResponse toResponse(Item item) {
        ItemDtoResponse itemDtoResponse = new ItemDtoResponse();
        itemDtoResponse.setId(item.getId());
        itemDtoResponse.setName(item.getName());
        itemDtoResponse.setDescription(item.getDescription());
        itemDtoResponse.setAvailable(item.getAvailable());
        itemDtoResponse.setOwnerId(item.getOwnerId());
        return itemDtoResponse;
    }
}
