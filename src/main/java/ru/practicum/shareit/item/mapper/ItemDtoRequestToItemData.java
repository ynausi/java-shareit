package ru.practicum.shareit.item.mapper;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.item.model.Item;


@Repository
public class ItemDtoRequestToItemData {
    public Item toData(ItemDtoRequest itemDtoRequest) {
        Item item = new Item();
        item.setName(itemDtoRequest.getName());
        item.setDescription(itemDtoRequest.getDescription());
        item.setAvailable(itemDtoRequest.getAvailable());
        return item;
    }
}
