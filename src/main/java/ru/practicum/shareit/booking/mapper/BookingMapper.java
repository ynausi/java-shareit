package ru.practicum.shareit.booking.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookerResponse;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.dto.ItemShortResponse;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusValue;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@Component
public class BookingMapper {

    public BookingResponse toResponse(Booking booking) {
        BookingResponse bookingResponse = new BookingResponse();
        bookingResponse.setId(booking.getId());
        bookingResponse.setStart(booking.getStartTime());
        bookingResponse.setEnd(booking.getEndTime());
        bookingResponse.setStatus(booking.getStatus());

        ItemShortResponse itemShortResponse = new ItemShortResponse();
        itemShortResponse.setId(booking.getItem().getId());
        itemShortResponse.setName(booking.getItem().getName());
        bookingResponse.setItem(itemShortResponse);

        BookerResponse bookerResponse = new BookerResponse();
        bookerResponse.setId(booking.getBooker().getId());
        bookingResponse.setBooker(bookerResponse);

        return bookingResponse;
    }

    public Booking toEntity(BookingRequest bookingRequest, User booker, Item item) {
        Booking booking = new Booking();
        booking.setBooker(booker);
        booking.setStartTime(bookingRequest.getStart());
        booking.setEndTime(bookingRequest.getEnd());
        booking.setItem(item);
        booking.setStatus(StatusValue.WAITING);
        return booking;
    }
}
