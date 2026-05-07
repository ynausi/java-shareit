package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.model.StatusValue;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.InternalServerException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.CommentRequest;
import ru.practicum.shareit.item.dto.CommentResponse;
import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.dto.ItemPatchDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemMapper itemMapper;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private CommentMapper commentMapper;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    @Test
    void shouldFindAllItems() {
        Item item = item(10, user(1), true);
        ItemDtoResponse response = itemResponse(10);

        when(itemRepository.findAll()).thenReturn(List.of(item));
        when(itemMapper.toResponse(item)).thenReturn(response);

        Collection<ItemDtoResponse> result = itemService.findAll();

        assertEquals(1, result.size());
    }

    @Test
    void shouldFindUserItems() {
        int userId = 1;

        Item item = item(10, user(userId), true);
        ItemDtoResponse response = itemResponse(10);

        when(itemRepository.findByUserId(userId)).thenReturn(List.of(item));
        when(itemMapper.toResponse(item)).thenReturn(response);

        Collection<ItemDtoResponse> result = itemService.findUserItems(userId);

        assertEquals(1, result.size());
    }

    @Test
    void shouldReturnEmptyListWhenSearchTextIsBlank() {
        Collection<ItemDtoResponse> result = itemService.findByName(" ");

        assertTrue(result.isEmpty());

        verify(itemRepository, never()).findByNameIgnoreCaseAndAvailableTrue(anyString());
    }

    @Test
    void shouldFindItemsByName() {
        Item item = item(10, user(1), true);
        ItemDtoResponse response = itemResponse(10);

        when(itemRepository.findByNameIgnoreCaseAndAvailableTrue("Дрель")).thenReturn(List.of(item));
        when(itemMapper.toResponse(item)).thenReturn(response);

        Collection<ItemDtoResponse> result = itemService.findByName("Дрель");

        assertEquals(1, result.size());
    }

    @Test
    void shouldFindItemById() {
        int itemId = 10;

        Item item = item(itemId, user(1), true);
        ItemDtoResponse response = itemResponse(itemId);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(itemMapper.toResponse(item)).thenReturn(response);

        ItemDtoResponse actual = itemService.findById(itemId);

        assertSame(response, actual);
    }

    @Test
    void shouldThrowWhenItemNotFoundById() {
        int itemId = 10;

        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.findById(itemId));
    }

    @Test
    void shouldSaveItemWithoutRequestId() {
        int userId = 1;

        User user = user(userId);
        ItemDtoRequest request = itemRequest(null);
        Item item = item(10, user, true);
        ItemDtoResponse response = itemResponse(10);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemMapper.toData(request)).thenReturn(item);
        when(itemRepository.save(item)).thenReturn(item);
        when(itemMapper.toResponse(item)).thenReturn(response);

        ItemDtoResponse actual = itemService.save(request, userId);

        assertSame(response, actual);
        assertSame(user, item.getUser());
    }

    @Test
    void shouldSaveItemWithRequestId() {
        int userId = 1;
        int requestId = 20;

        User user = user(userId);
        ItemDtoRequest request = itemRequest(requestId);
        Item item = item(10, user, true);
        ItemRequest itemRequest = new ItemRequest();
        ItemDtoResponse response = itemResponse(10);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemMapper.toData(request)).thenReturn(item);
        when(itemRequestRepository.findById(requestId)).thenReturn(Optional.of(itemRequest));
        when(itemRepository.save(item)).thenReturn(item);
        when(itemMapper.toResponse(item)).thenReturn(response);

        ItemDtoResponse actual = itemService.save(request, userId);

        assertSame(response, actual);
        assertSame(itemRequest, item.getItemRequest());
    }

    @Test
    void shouldThrowWhenUserNotFoundOnSaveItem() {
        int userId = 1;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.save(itemRequest(null), userId));

        verify(itemRepository, never()).save(any());
    }

    @Test
    void shouldUpdateItemAllFields() {
        int userId = 1;
        int itemId = 10;

        User user = user(userId);
        Item item = item(itemId, user, true);
        ItemPatchDto patch = new ItemPatchDto();
        patch.setName("Новое имя");
        patch.setDescription("Новое описание");
        patch.setAvailable(false);

        ItemDtoResponse response = itemResponse(itemId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(itemRepository.save(item)).thenReturn(item);
        when(itemMapper.toResponse(item)).thenReturn(response);

        ItemDtoResponse actual = itemService.update(patch, itemId, userId);

        assertSame(response, actual);
        assertEquals("Новое имя", item.getName());
        assertEquals("Новое описание", item.getDescription());
        assertFalse(item.getAvailable());
    }

    @Test
    void shouldNotUpdateBlankOrNullFields() {
        int userId = 1;
        int itemId = 10;

        User user = user(userId);
        Item item = item(itemId, user, true);
        ItemPatchDto patch = new ItemPatchDto();
        patch.setName("");
        patch.setDescription(null);
        patch.setAvailable(null);

        ItemDtoResponse response = itemResponse(itemId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(itemRepository.save(item)).thenReturn(item);
        when(itemMapper.toResponse(item)).thenReturn(response);

        itemService.update(patch, itemId, userId);

        assertEquals("Item 10", item.getName());
        assertEquals("Description", item.getDescription());
        assertTrue(item.getAvailable());
    }

    @Test
    void shouldThrowWhenUserNotFoundOnUpdate() {
        int userId = 1;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.update(new ItemPatchDto(), 10, userId));
    }

    @Test
    void shouldThrowWhenItemNotFoundOnUpdate() {
        int userId = 1;
        int itemId = 10;

        when(userRepository.findById(userId)).thenReturn(Optional.of(user(userId)));
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.update(new ItemPatchDto(), itemId, userId));
    }

    @Test
    void shouldSaveComment() {
        int userId = 1;
        int itemId = 10;

        User user = user(userId);
        Item item = item(itemId, user(2), true);
        CommentRequest request = new CommentRequest();
        request.setText("Отличная вещь");

        Comment comment = new Comment();
        CommentResponse response = new CommentResponse();
        response.setId(1);
        response.setText("Отличная вещь");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.existsByItem_IdAndBooker_Id_AndStatusAndEndTimeBefore(
                eq(itemId), eq(userId), eq(StatusValue.APPROVED), any(LocalDateTime.class)))
                .thenReturn(true);
        when(commentMapper.toData(request)).thenReturn(comment);
        when(commentRepository.save(comment)).thenReturn(comment);
        when(commentMapper.toResponse(comment)).thenReturn(response);

        CommentResponse actual = itemService.saveComment(request, userId, itemId);

        assertSame(response, actual);
        assertSame(item, comment.getItem());
        assertSame(user, comment.getAuthor());
    }

    @Test
    void shouldThrowWhenBookingHasNotFinishedOnComment() {
        int userId = 1;
        int itemId = 10;

        when(userRepository.findById(userId)).thenReturn(Optional.of(user(userId)));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item(itemId, user(2), true)));
        when(bookingRepository.existsByItem_IdAndBooker_Id_AndStatusAndEndTimeBefore(
                eq(itemId), eq(userId), eq(StatusValue.APPROVED), any(LocalDateTime.class)))
                .thenReturn(false);

        assertThrows(InternalServerException.class,
                () -> itemService.saveComment(new CommentRequest(), userId, itemId));
    }

    @Test
    void shouldFindCommentById() {
        int commentId = 1;

        Comment comment = new Comment();
        CommentResponse response = new CommentResponse();
        response.setId(commentId);

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        when(commentMapper.toResponse(comment)).thenReturn(response);

        CommentResponse actual = itemService.findCommentById(commentId);

        assertSame(response, actual);
    }

    @Test
    void shouldThrowWhenCommentNotFound() {
        int commentId = 1;

        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.findCommentById(commentId));
    }

    @Test
    void shouldDeleteItem() {
        int itemId = 10;
        Item item = item(itemId, user(1), true);

        when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(item))
                .thenReturn(Optional.empty());

        itemService.delete(itemId);

        verify(itemRepository).deleteById(itemId);
    }

    @Test
    void shouldThrowWhenItemNotFoundOnDelete() {
        int itemId = 10;

        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.delete(itemId));

        verify(itemRepository, never()).deleteById(anyInt());
    }

    @Test
    void shouldThrowWhenItemWasNotDeleted() {
        int itemId = 10;
        Item item = item(itemId, user(1), true);

        when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(item))
                .thenReturn(Optional.of(item));

        assertThrows(InternalServerException.class, () -> itemService.delete(itemId));
    }

    private User user(int id) {
        User user = new User();
        user.setId(id);
        user.setName("User " + id);
        user.setEmail("user" + id + "@mail.com");
        return user;
    }

    private Item item(int id, User owner, boolean available) {
        Item item = new Item();
        item.setId(id);
        item.setName("Item " + id);
        item.setDescription("Description");
        item.setAvailable(available);
        item.setUser(owner);
        return item;
    }

    private ItemDtoRequest itemRequest(Integer requestId) {
        ItemDtoRequest request = new ItemDtoRequest();
        request.setName("Дрель");
        request.setDescription("Обычная дрель");
        request.setAvailable(true);
        request.setRequestId(requestId);
        return request;
    }

    private ItemDtoResponse itemResponse(int id) {
        ItemDtoResponse response = new ItemDtoResponse();
        response.setId(id);
        response.setName("Item " + id);
        response.setDescription("Description");
        response.setAvailable(true);
        return response;
    }
}