package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.Optional;

public interface ItemRepository {

    Collection<Item> findAll();

    Collection<Item> searchUsersItems(int ownerId);

    Collection<Item> searchByName(String name);

    Optional<Item> findById(int id);

    Item save(Item item, int ownerId);

    Item update(Item item);

    void delete(int id);
}
