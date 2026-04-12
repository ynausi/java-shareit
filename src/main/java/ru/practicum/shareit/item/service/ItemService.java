package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.dto.ItemPatchDto;

import java.util.Collection;

public interface ItemService {

    Collection<ItemDtoResponse> findAll();

    Collection<ItemDtoResponse> searchUserItems(int userId);

    Collection<ItemDtoResponse> searchByName(String name);

    ItemDtoResponse findById(int id);

    ItemDtoResponse save(ItemDtoRequest item, int ownerId);

    ItemDtoResponse update(ItemPatchDto item,int itemId,int ownerId);

    void delete(int id);
}
