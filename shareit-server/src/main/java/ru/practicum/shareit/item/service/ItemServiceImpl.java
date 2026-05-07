package ru.practicum.shareit.item.service;


import lombok.RequiredArgsConstructor;
import org.springframework.boot.logging.LogLevel;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.annotations.Loggable;
import ru.practicum.shareit.booking.model.StatusValue;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.InternalServerException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemMapper itemMapper;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final BookingRepository bookingRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    public Collection<ItemDtoResponse> findAll() {
        return itemRepository.findAll().stream()
                .map(itemMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ItemDtoResponse> findUserItems(int userId) {
        return itemRepository.findByUserId(userId).stream()
                .map(itemMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ItemDtoResponse> findByName(String name) {
        if (name.isBlank()) return Collections.emptyList();
        return itemRepository.findByNameIgnoreCaseAndAvailableTrue(name).stream()
                .map(itemMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDtoResponse findById(int itemId) {
        return itemRepository.findById(itemId)
                .map(itemMapper::toResponse)
                .orElseThrow(
                () -> new NotFoundException("No item with id:" + itemId));
    }

    @Loggable(value = "save",level = LogLevel.DEBUG)
    @Override
    public ItemDtoResponse save(ItemDtoRequest itemDtoRequest, int userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("No user with id:" + userId)
        );
        Item item = itemMapper.toData(itemDtoRequest);
        item.setUser(user);
        if (itemDtoRequest.getRequestId() != null) {
            ItemRequest itemRequest = itemRequestRepository.findById(itemDtoRequest.getRequestId())
                    .orElseThrow(() -> new NotFoundException("No such request with id:" + itemDtoRequest.getRequestId()));
            item.setItemRequest(itemRequest);
        }
        Item created = itemRepository.save(item);
        return itemMapper.toResponse(created);
    }

    @Override
    public ItemDtoResponse update(ItemPatchDto itemPatchDtoRequest,int itemId,int userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("No user with id:" + userId)
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
        item = itemRepository.save(item);
        return itemMapper.toResponse(item);
    }

    @Override
    public CommentResponse saveComment(CommentRequest itemCommentRequest, int userId, int itemId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("No user with id:" + userId)
        );
        Item item = itemRepository.findById(itemId).orElseThrow(
                () -> new NotFoundException("No item with id:" + itemId)
        );

        boolean bookingHasFinished = bookingRepository
                .existsByItem_IdAndBooker_Id_AndStatusAndEndTimeBefore(itemId,userId, StatusValue.APPROVED, LocalDateTime.now());

        if (!bookingHasFinished) {
            throw new InternalServerException("You can't comment busy item");
        }

        Comment comment = commentRepository.save(commentMapper.toData(itemCommentRequest));
        comment.setItem(item);
        comment.setAuthor(user);
        Comment created = commentRepository.save(comment);

        return commentMapper.toResponse(created);
    }

    @Override
    public CommentResponse findCommentById(int commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new NotFoundException("No comment with id:" + commentId)
        );
        return commentMapper.toResponse(comment);
    }


    @Override
    public void delete(int itemId) {
        itemRepository.findById(itemId).orElseThrow(
                () -> new NotFoundException("No item with id:" + itemId)
        );
        itemRepository.deleteById(itemId);
        if (itemRepository.findById(itemId).isPresent()) {
            throw new InternalServerException("Не удалось удалить предмет");
        }
    }
}
