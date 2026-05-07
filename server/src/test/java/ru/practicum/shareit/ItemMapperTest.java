package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.StatusValue;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.CommentResponse;
import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.request.dto.ItemRequestDtoForResponse;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemMapperTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private CommentMapper commentMapper;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private BookingMapper bookingMapper;

    @InjectMocks
    private ItemMapper itemMapper;

    @Test
    void shouldMapRequestDtoToEntity() {
        ItemDtoRequest request = new ItemDtoRequest();
        request.setName("Дрель");
        request.setDescription("Обычная дрель");
        request.setAvailable(true);

        Item item = itemMapper.toData(request);

        assertEquals("Дрель", item.getName());
        assertEquals("Обычная дрель", item.getDescription());
        assertTrue(item.getAvailable());
    }

    @Test
    void shouldMapEntityToResponse() {
        User owner = new User();
        owner.setId(1);

        Item item = new Item();
        item.setId(10);
        item.setName("Дрель");
        item.setDescription("Обычная дрель");
        item.setAvailable(true);
        item.setUser(owner);

        Comment comment = new Comment();
        CommentResponse commentResponse = new CommentResponse();
        commentResponse.setId(100);
        commentResponse.setText("Хорошая вещь");

        when(commentRepository.findByItem_IdOrderByCreatedAsc(item.getId()))
                .thenReturn(List.of(comment));
        when(commentMapper.toResponse(comment))
                .thenReturn(commentResponse);
        when(bookingRepository.findFirstByItem_IdAndStatusAndEndTimeBeforeOrderByEndTimeDesc(
                eq(item.getId()),
                eq(StatusValue.APPROVED),
                any(LocalDateTime.class)
        )).thenReturn(Optional.empty());
        when(bookingRepository.findFirstByItem_IdAndStatusAndStartTimeAfterOrderByStartTimeAsc(
                eq(item.getId()),
                eq(StatusValue.APPROVED),
                any(LocalDateTime.class)
        )).thenReturn(Optional.empty());

        ItemDtoResponse response = itemMapper.toResponse(item);

        assertEquals(10, response.getId());
        assertEquals("Дрель", response.getName());
        assertEquals("Обычная дрель", response.getDescription());
        assertTrue(response.getAvailable());
        assertEquals(1, response.getUserId());
        assertEquals(1, response.getComments().size());
        assertEquals("Хорошая вещь", response.getComments().getFirst().getText());

        verify(commentRepository).findByItem_IdOrderByCreatedAsc(item.getId());
        verify(commentMapper).toResponse(comment);
    }

    @Test
    void shouldMapItemToItemRequestDtoForResponse() {
        User owner = new User();
        owner.setId(1);

        Item item = new Item();
        item.setId(10);
        item.setName("Дрель");
        item.setUser(owner);

        ItemRequestDtoForResponse response = itemMapper.toItemRequestDtoForResponse(item);

        assertEquals(10, response.getId());
        assertEquals("Дрель", response.getName());
        assertEquals(1, response.getOwnerId());
    }
}