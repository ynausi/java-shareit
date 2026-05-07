package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.*;

import java.util.Collection;

public interface ItemService {

    Collection<ItemDtoResponse> findAll();

    Collection<ItemDtoResponse> findUserItems(int userId);

    Collection<ItemDtoResponse> findByName(String name);

    ItemDtoResponse findById(int itemId);

    ItemDtoResponse save(ItemDtoRequest item, int userId);

    ItemDtoResponse update(ItemPatchDto item,int itemId,int userId);

    CommentResponse saveComment(CommentRequest itemCommentRequest, int userId, int itemId);

    CommentResponse findCommentById(int commentId);

    void delete(int itemId);
}
