package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Item;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item,Integer> {

    Collection<Item> findByUserId(int userId);

    Collection<Item> findByNameIgnoreCaseAndAvailableTrue(String name);

    Optional<Item> findById(int itemId);

    List<Item> findAllByItemRequest_Id(Integer requestId);

    void deleteById(int itemId);
}
