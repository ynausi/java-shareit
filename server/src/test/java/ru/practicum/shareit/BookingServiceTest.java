package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusValue;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exceptions.InternalServerException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingMapper bookingMapper;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Test
    void shouldCreateBookingWhenRequestIsValid() {
        int userId = 1;
        int itemId = 10;

        User booker = user(userId);
        User owner = user(2);
        Item item = item(itemId, owner, true);

        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        BookingRequest request = new BookingRequest(itemId, start, end);
        Booking booking = booking(100, booker, item);
        BookingResponse response = response(100);

        when(userRepository.findById(userId)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(request.getItemId()))
                .thenReturn(Optional.of(item));
        when(bookingMapper.toEntity(request, booker, item)).thenReturn(booking);
        when(bookingRepository.save(booking)).thenReturn(booking);
        when(bookingMapper.toResponse(booking)).thenReturn(response);

        BookingResponse actual = bookingService.save(request, userId);

        assertSame(response, actual);

        verify(userRepository).findById(userId);
        verify(itemRepository).findById(request.getItemId());
        verify(bookingRepository).save(booking);
    }

    @Test
    void shouldThrowWhenBookerNotFoundOnCreate() {
        int userId = 1;
        BookingRequest request = new BookingRequest(
                10,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2)
        );

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.save(request, userId));

        verify(itemRepository, never()).findById(anyInt());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenItemNotFoundOnCreate() {
        int userId = 1;
        int itemId = 10;

        BookingRequest request = new BookingRequest(
                itemId,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2)
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(user(userId)));
        when(itemRepository.findById(request.getItemId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.save(request, userId));

        verify(bookingRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenStartEqualsEnd() {
        int userId = 1;
        int itemId = 10;

        LocalDateTime time = LocalDateTime.now().plusDays(1);
        BookingRequest request = new BookingRequest(itemId, time, time);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user(userId)));
        when(itemRepository.findById(request.getItemId())).thenReturn(Optional.of(item(itemId, user(2), true)));

        assertThrows(IllegalArgumentException.class, () -> bookingService.save(request, userId));
    }

    @Test
    void shouldThrowWhenEndIsInPast() {
        int userId = 1;
        int itemId = 10;

        BookingRequest request = new BookingRequest(
                itemId,
                LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1)
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(user(userId)));
        when(itemRepository.findById(request.getItemId())).thenReturn(Optional.of(item(itemId, user(2), true)));

        assertThrows(IllegalArgumentException.class, () -> bookingService.save(request, userId));
    }

    @Test
    void shouldThrowWhenStartIsInPast() {
        int userId = 1;
        int itemId = 10;

        BookingRequest request = new BookingRequest(
                itemId,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(1)
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(user(userId)));
        when(itemRepository.findById(request.getItemId())).thenReturn(Optional.of(item(itemId, user(2), true)));

        assertThrows(IllegalArgumentException.class, () -> bookingService.save(request, userId));
    }

    @Test
    void shouldThrowWhenItemUnavailable() {
        int userId = 1;
        int itemId = 10;

        BookingRequest request = new BookingRequest(
                itemId,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2)
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(user(userId)));
        when(itemRepository.findById(request.getItemId())).thenReturn(Optional.of(item(itemId, user(2), false)));

        assertThrows(InternalServerException.class, () -> bookingService.save(request, userId));
    }

    @Test
    void shouldFindBookingByIdWhenUserIsBooker() {
        int bookingId = 100;
        int userId = 1;

        User booker = user(userId);
        User owner = user(2);
        Item item = item(10, owner, true);
        Booking booking = booking(bookingId, booker, item);
        BookingResponse response = response(bookingId);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(userRepository.findById(userId)).thenReturn(Optional.of(booker));
        when(bookingMapper.toResponse(booking)).thenReturn(response);

        BookingResponse actual = bookingService.findBookingById(bookingId, userId);

        assertSame(response, actual);
    }

    @Test
    void shouldThrowWhenUserIsNotBookerOrOwner() {
        int bookingId = 100;
        int userId = 3;

        User booker = user(1);
        User owner = user(2);
        User outsider = user(userId);

        Item item = item(10, owner, true);
        Booking booking = booking(bookingId, booker, item);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(userRepository.findById(userId)).thenReturn(Optional.of(outsider));

        assertThrows(NotFoundException.class, () -> bookingService.findBookingById(bookingId, userId));
    }

    @Test
    void shouldApproveBooking() {
        int bookingId = 100;
        int userId = 2;

        User booker = user(1);
        User owner = user(userId);
        Item item = item(10, owner, true);
        Booking booking = booking(bookingId, booker, item);
        BookingResponse response = response(bookingId);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(userRepository.findById(userId)).thenReturn(Optional.of(owner));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(bookingRepository.save(booking)).thenReturn(booking);
        when(bookingMapper.toResponse(booking)).thenReturn(response);

        BookingResponse actual = bookingService.update(bookingId, true, userId);

        assertSame(response, actual);
        assertEquals(StatusValue.APPROVED, booking.getStatus());
    }

    @Test
    void shouldRejectBooking() {
        int bookingId = 100;
        int userId = 2;

        User booker = user(1);
        User owner = user(userId);
        Item item = item(10, owner, true);
        Booking booking = booking(bookingId, booker, item);
        BookingResponse response = response(bookingId);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(userRepository.findById(userId)).thenReturn(Optional.of(owner));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(bookingRepository.save(booking)).thenReturn(booking);
        when(bookingMapper.toResponse(booking)).thenReturn(response);

        BookingResponse actual = bookingService.update(bookingId, false, userId);

        assertSame(response, actual);
        assertEquals(StatusValue.REJECTED, booking.getStatus());
    }

    @Test
    void shouldThrowWhenApprovedIsNull() {
        int bookingId = 100;
        int userId = 2;

        User owner = user(userId);
        Item item = item(10, owner, true);
        Booking booking = booking(bookingId, user(1), item);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(userRepository.findById(userId)).thenReturn(Optional.of(owner));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        assertThrows(InternalServerException.class, () -> bookingService.update(bookingId, null, userId));
    }

    @ParameterizedTest
    @ValueSource(strings = {"ALL", "CURRENT", "PAST", "FUTURE", "REJECTED", "APPROVED", "WAITING"})
    void shouldFindAllUserBookingsByState(String state) {
        int userId = 1;

        User booker = user(userId);
        Item item = item(10, user(2), true);
        Booking booking = booking(100, booker, item);
        BookingResponse response = response(100);

        when(userRepository.findById(userId)).thenReturn(Optional.of(booker));
        stubBookerBookings(userId, state, booking);
        when(bookingMapper.toResponse(booking)).thenReturn(response);

        Collection<BookingResponse> result = bookingService.findAllUserBookings(userId, state);

        assertEquals(1, result.size());
    }

    @Test
    void shouldThrowWhenUserBookingStateUnknown() {
        int userId = 1;

        when(userRepository.findById(userId)).thenReturn(Optional.of(user(userId)));

        assertThrows(IllegalArgumentException.class,
                () -> bookingService.findAllUserBookings(userId, "UNKNOWN"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"ALL", "CURRENT", "PAST", "FUTURE", "REJECTED", "APPROVED", "WAITING"})
    void shouldFindOwnerBookingsByState(String state) {
        int ownerId = 2;

        User owner = user(ownerId);
        Item item = item(10, owner, true);
        Booking booking = booking(100, user(1), item);
        BookingResponse response = response(100);

        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        stubOwnerBookings(ownerId, state, booking);
        when(bookingMapper.toResponse(booking)).thenReturn(response);

        Collection<BookingResponse> result = bookingService.findAllUserItemWhichAreBooked(ownerId, state);

        assertEquals(1, result.size());
    }

    @Test
    void shouldThrowWhenOwnerBookingStateUnknown() {
        int userId = 1;

        when(userRepository.findById(userId)).thenReturn(Optional.of(user(userId)));

        assertThrows(IllegalArgumentException.class,
                () -> bookingService.findAllUserItemWhichAreBooked(userId, "UNKNOWN"));
    }

    private void stubBookerBookings(int userId, String state, Booking booking) {
        switch (state) {
            case "ALL" -> when(bookingRepository.findByBooker_IdOrderByStartTimeDesc(userId))
                    .thenReturn(List.of(booking));
            case "CURRENT" -> when(bookingRepository
                    .findByBooker_IdAndStartTimeBeforeAndEndTimeAfterOrderByStartTimeDesc(
                            eq(userId), any(Instant.class), any(Instant.class)))
                    .thenReturn(List.of(booking));
            case "PAST" -> when(bookingRepository
                    .findByBooker_IdAndEndTimeBeforeOrderByStartTimeDesc(eq(userId), any(Instant.class)))
                    .thenReturn(List.of(booking));
            case "FUTURE" -> when(bookingRepository
                    .findByBooker_IdAndStartTimeAfterOrderByStartTimeDesc(eq(userId), any(Instant.class)))
                    .thenReturn(List.of(booking));
            default -> when(bookingRepository
                    .findByBooker_IdAndStatusOrderByStartTimeDesc(eq(userId), eq(StatusValue.valueOf(state))))
                    .thenReturn(List.of(booking));
        }
    }

    private void stubOwnerBookings(int userId, String state, Booking booking) {
        switch (state) {
            case "ALL" -> when(bookingRepository.findByItem_User_IdOrderByStartTimeDesc(userId))
                    .thenReturn(List.of(booking));
            case "CURRENT" -> when(bookingRepository
                    .findByItem_User_IdAndStartTimeBeforeAndEndTimeAfterOrderByStartTimeDesc(
                            eq(userId), any(Instant.class), any(Instant.class)))
                    .thenReturn(List.of(booking));
            case "PAST" -> when(bookingRepository
                    .findByItem_User_IdAndEndTimeBeforeOrderByStartTimeDesc(eq(userId), any(Instant.class)))
                    .thenReturn(List.of(booking));
            case "FUTURE" -> when(bookingRepository
                    .findByItem_User_IdAndStartTimeAfterOrderByStartTimeDesc(eq(userId), any(Instant.class)))
                    .thenReturn(List.of(booking));
            default -> when(bookingRepository
                    .findByItem_User_IdAndStatusOrderByStartTimeDesc(eq(userId), eq(StatusValue.valueOf(state))))
                    .thenReturn(List.of(booking));
        }
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

    private Booking booking(int id, User booker, Item item) {
        Booking booking = new Booking();
        booking.setId(id);
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(StatusValue.WAITING);
        return booking;
    }

    private BookingResponse response(int id) {
        BookingResponse response = new BookingResponse();
        response.setId(id);
        return response;
    }
}
