package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDtoForResponse;
import ru.practicum.shareit.request.dto.ItemRequestReq;
import ru.practicum.shareit.request.dto.ItemRequestResponse;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemRequestMapperTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ItemMapper itemMapper;

    @InjectMocks
    private ItemRequestMapper itemRequestMapper;

    @Test
    void shouldMapRequestDtoToEntity() {
        ItemRequestReq request = new ItemRequestReq();
        request.setDescription("Хочу дрель");

        ItemRequest itemRequest = itemRequestMapper.toData(request);

        assertEquals("Хочу дрель", itemRequest.getDescription());
        assertNotNull(itemRequest.getCreatedTime());
    }

    @Test
    void shouldMapEntityToResponse() {
        int requestId = 10;

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(requestId);
        itemRequest.setDescription("Хочу дрель");
        itemRequest.setCreatedTime(LocalDateTime.of(2026, 5, 7, 10, 0));

        User owner = new User();
        owner.setId(1);

        Item item = new Item();
        item.setId(100);
        item.setName("Дрель");
        item.setUser(owner);

        ItemRequestDtoForResponse itemResponse = new ItemRequestDtoForResponse();
        itemResponse.setId(100);
        itemResponse.setName("Дрель");
        itemResponse.setOwnerId(1);

        when(itemRepository.findAllByItemRequest_Id(requestId))
                .thenReturn(List.of(item));
        when(itemMapper.toItemRequestDtoForResponse(item))
                .thenReturn(itemResponse);

        ItemRequestResponse response = itemRequestMapper.toResponse(itemRequest);

        assertEquals(requestId, response.getId());
        assertEquals("Хочу дрель", response.getDescription());
        assertEquals(LocalDateTime.of(2026, 5, 7, 10, 0), response.getCreated());
        assertEquals(1, response.getItems().size());
        assertEquals(100, response.getItems().getFirst().getId());
        assertEquals("Дрель", response.getItems().getFirst().getName());
        assertEquals(1, response.getItems().getFirst().getOwnerId());

        verify(itemRepository).findAllByItemRequest_Id(requestId);
        verify(itemMapper).toItemRequestDtoForResponse(item);
    }
}