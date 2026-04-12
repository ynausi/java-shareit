package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.InternalServerException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.dto.ItemPatchDto;
import ru.practicum.shareit.item.mapper.ItemDataToItemResponse;
import ru.practicum.shareit.item.mapper.ItemDtoRequestToItemData;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemDataToItemResponse itemDataToItemResponse;
    private final ItemDtoRequestToItemData itemDtoRequestToItemData;

    @Override
    public Collection<ItemDtoResponse> findAll() {
        return itemRepository.findAll().stream()
                .map(itemDataToItemResponse::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ItemDtoResponse> searchUserItems(int userId) {
        return itemRepository.searchUsersItems(userId).stream()
                .map(itemDataToItemResponse::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ItemDtoResponse> searchByName(String name) {
        if (name.isBlank()) return Collections.emptyList();
        return itemRepository.searchByName(name).stream()
                .map(itemDataToItemResponse::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDtoResponse findById(int id) {
        return itemRepository.findById(id)
                .map(itemDataToItemResponse::toResponse)
                .orElseThrow(
                () -> new NotFoundException("No item with id:" + id));
    }

    @Override
    public ItemDtoResponse save(ItemDtoRequest itemDtoRequest, int ownerId) {
        userRepository.findById(ownerId).orElseThrow(
                () -> new NotFoundException("No user with id:" + ownerId)
        );
        Item item = itemDtoRequestToItemData.toData(itemDtoRequest);
        Item created = itemRepository.save(item,ownerId);
        return itemDataToItemResponse.toResponse(created);
    }

    @Override
    public ItemDtoResponse update(ItemPatchDto itemPatchDtoRequest,int itemId,int ownerId) {
        User user = userRepository.findById(ownerId).orElseThrow(
                () -> new NotFoundException("No user with id:" + ownerId)
        );
        Item item = itemRepository.findById(itemId).orElseThrow(
                () -> new NotFoundException("No item with id:" + itemId)
        );
        if (itemPatchDtoRequest.getName() != null && !itemPatchDtoRequest.getName().isBlank()) {
            item.setName(itemPatchDtoRequest.getName());
        }
        if (itemPatchDtoRequest.getDescription() != null && !itemPatchDtoRequest.getDescription().isBlank()) {
            item.setDescription(itemPatchDtoRequest.getDescription());
        }
        if (itemPatchDtoRequest.getAvailable() != null) {
            item.setAvailable(itemPatchDtoRequest.getAvailable());
        }
        item = itemRepository.update(item);
        return itemDataToItemResponse.toResponse(item);
    }

    @Override
    public void delete(int id) {
        itemRepository.findById(id).orElseThrow(
                () -> new NotFoundException("No item with id:" + id)
        );
        itemRepository.delete(id);
        if (itemRepository.findById(id).isPresent()) {
            throw new InternalServerException("Не удалось удалить предмет");
        }
    }
}
