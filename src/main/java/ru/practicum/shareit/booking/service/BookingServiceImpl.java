package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusValue;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.InternalServerException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService{
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final BookingMapper bookingMapper;
    private final ItemRepository itemRepository;

    @Override
    public BookingResponse findBookingById(int bookingId,int userId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new NotFoundException("No booking with id:" + bookingId)
        );
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("No user with id:" + userId)
        );
        if (!Objects.equals(user.getId(), booking.getItem().getUser().getId()) &&
                !Objects.equals(user.getId(), booking.getBooker().getId())) {
            throw new NotFoundException("You are not Booker or Owner of this Item");
        }
        return bookingMapper.toResponse(booking);
    }

    @Override
    public BookingResponse save(BookingRequest bookingRequest,int userId) {

        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("No user with id:" + userId)
        );
        Item item = itemRepository.findById(bookingRequest.getItemId()).orElseThrow(
                () -> new NotFoundException("No item with id:" + bookingRequest.getItemId())
        );
        if (bookingRequest.getStart().equals(bookingRequest.getEnd())) throw new IllegalArgumentException("startTime can't be equal to endTime ");
        if (bookingRequest.getEnd().isBefore(LocalDateTime.now())) throw new IllegalArgumentException("endTime can't be int the past");
        if (bookingRequest.getStart().isBefore(LocalDateTime.now())) throw new IllegalArgumentException("startTime can't be in the past");
        if (!item.getAvailable()) throw new InternalServerException("You cannot book unavailable Item");
        Booking booking = bookingMapper.toEntity(bookingRequest, user, item);
        Booking saved = bookingRepository.save(booking);
        return bookingMapper.toResponse(saved);
    }

    @Override
    public BookingResponse update(int bookingId, Boolean approved,int userId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new NotFoundException("No booking with id:" + bookingId)
        );
        User user = userRepository.findById(userId).orElseThrow(
                () -> new InternalServerException("No user with id:" + userId)
        );
        Integer itemId = booking.getItem().getId();
        Item item = itemRepository.findById(booking.getItem().getId()).orElseThrow(
                () -> new NotFoundException("No item with id" + itemId)
        );
        if (approved == null) {
            throw new InternalServerException("You must put FALSE or TRUE in your request");
        }

        if (approved) booking.setStatus(StatusValue.APPROVED);
        if(!approved) booking.setStatus(StatusValue.REJECTED);
        booking = bookingRepository.save(booking);
        return bookingMapper.toResponse(booking);
    }

    @Override
    public BookingResponse approveOrRejectBooking(int bookingId) {
        return null;
    }

    @Override
    public Collection<BookingResponse> findAllUserBookings(int userId,String state) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("No user with id:" + userId)
        );
        Collection<Booking> bookings = new ArrayList<>();
        switch (state) {
            case "ALL":
                bookings = bookingRepository
                        .findByBooker_IdOrderByStartTimeDesc(userId);
                break;
            case "CURRENT":
                Instant now = Instant.now();
                bookings = bookingRepository
                        .findByBooker_IdAndStartTimeBeforeAndEndTimeAfterOrderByStartTimeDesc(userId, now,now);
                break;
            case "PAST":
                bookings= bookingRepository
                        .findByBooker_IdAndEndTimeBeforeOrderByStartTimeDesc(userId,Instant.now());
                break;
            case "FUTURE":
                bookings = bookingRepository
                        .findByBooker_IdAndStartTimeAfterOrderByStartTimeDesc(userId,Instant.now());
                break;
            case "REJECTED", "APPROVED", "WAITING":
                bookings = bookingRepository
                        .findByBooker_IdAndStatusOrderByStartTimeDesc(userId,StatusValue.valueOf(state));
                break;
            default:
                 new IllegalArgumentException("Unkown state: " + state);
        }
        return bookings.stream()
                .map(bookingMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<BookingResponse> findAllUserItemWhichAreBooked(int userId,String state) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("No user with id:" + userId)
        );
        Collection<Booking> bookings = new ArrayList<>();
        switch (state) {
            case "ALL":
                bookings = bookingRepository
                        .findByItem_User_IdOrderByStartTimeDesc(userId);
                break;
            case "CURRENT":
                Instant now = Instant.now();
                bookings = bookingRepository
                        .findByItem_User_IdAndStartTimeBeforeAndEndTimeAfterOrderByStartTimeDesc(userId, now,now);
                break;
            case "PAST":
                bookings= bookingRepository
                        .findByItem_User_IdAndEndTimeBeforeOrderByStartTimeDesc(userId,Instant.now());
                break;
            case "FUTURE":
                bookings = bookingRepository
                        .findByItem_User_IdAndStartTimeAfterOrderByStartTimeDesc(userId,Instant.now());
                break;
            case "REJECTED", "APPROVED", "WAITING":
                bookings = bookingRepository
                        .findByItem_User_IdAndStatusOrderByStartTimeDesc(userId,StatusValue.valueOf(state));
                break;
            default:
                new IllegalArgumentException("Unkown state: " + state);
        }
        return bookings.stream()
                .map(bookingMapper::toResponse)
                .collect(Collectors.toList());
    }
}
