package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestReq;
import ru.practicum.shareit.request.dto.ItemRequestResponse;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceTest {

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private ItemRequestMapper itemRequestMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    @Test
    void shouldFindUserRequests() {
        int userId = 1;

        ItemRequest itemRequest = itemRequest(10);
        ItemRequestResponse response = response(10);

        when(itemRequestRepository.findAllByUser_Id(userId))
                .thenReturn(List.of(itemRequest));
        when(itemRequestMapper.toResponse(itemRequest))
                .thenReturn(response);

        Collection<ItemRequestResponse> result = itemRequestService.findUserRequests(userId);

        assertEquals(1, result.size());
        assertSame(response, result.iterator().next());

        verify(itemRequestRepository).findAllByUser_Id(userId);
        verify(itemRequestMapper).toResponse(itemRequest);
    }

    @Test
    void shouldSaveItemRequest() {
        int userId = 1;

        User user = user(userId);

        ItemRequestReq request = new ItemRequestReq();
        request.setDescription("Хочу дрель");

        ItemRequest itemRequest = itemRequest(10);
        ItemRequestResponse response = response(10);

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        when(itemRequestMapper.toData(request))
                .thenReturn(itemRequest);
        when(itemRequestRepository.save(itemRequest))
                .thenReturn(itemRequest);
        when(itemRequestMapper.toResponse(itemRequest))
                .thenReturn(response);

        ItemRequestResponse actual = itemRequestService.save(request, userId);

        assertSame(response, actual);
        assertSame(user, itemRequest.getUser());

        verify(userRepository).findById(userId);
        verify(itemRequestRepository).save(itemRequest);
    }

    @Test
    void shouldThrowWhenUserNotFoundOnSave() {
        int userId = 1;

        ItemRequestReq request = new ItemRequestReq();
        request.setDescription("Хочу дрель");

        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemRequestService.save(request, userId));

        verify(itemRequestRepository, never()).save(any());
    }

    @Test
    void shouldFindRequestById() {
        int userId = 1;
        int requestId = 10;

        ItemRequest itemRequest = itemRequest(requestId);
        ItemRequestResponse response = response(requestId);

        when(itemRequestRepository.findById(requestId))
                .thenReturn(Optional.of(itemRequest));
        when(itemRequestMapper.toResponse(itemRequest))
                .thenReturn(response);

        ItemRequestResponse actual = itemRequestService.findByRequestId(requestId, userId);

        assertSame(response, actual);

        verify(itemRequestRepository).findById(requestId);
        verify(itemRequestMapper).toResponse(itemRequest);
    }

    @Test
    void shouldThrowWhenRequestNotFound() {
        int userId = 1;
        int requestId = 10;

        when(itemRequestRepository.findById(requestId))
                .thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> itemRequestService.findByRequestId(requestId, userId)
        );
    }

    private ItemRequest itemRequest(int id) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(id);
        itemRequest.setDescription("Хочу дрель");
        itemRequest.setCreatedTime(LocalDateTime.of(2026, 5, 7, 10, 0));
        return itemRequest;
    }

    private ItemRequestResponse response(int id) {
        ItemRequestResponse response = new ItemRequestResponse();
        response.setId(id);
        response.setDescription("Хочу дрель");
        response.setCreated(LocalDateTime.of(2026, 5, 7, 10, 0));
        return response;
    }

    private User user(int id) {
        User user = new User();
        user.setId(id);
        user.setName("User " + id);
        user.setEmail("user" + id + "@mail.com");
        return user;
    }
}
