package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.model.Booking;

import java.util.Collection;

public interface BookingService {

    BookingResponse findBookingById(int bookingId,int userId);

    BookingResponse save(BookingRequest bookingRequest,int userId);

    BookingResponse update(int bookingId, Boolean approved,int userId);

    BookingResponse approveOrRejectBooking(int bookingId);

    Collection<BookingResponse> findAllUserBookings(int userId,String state);

    Collection<BookingResponse> findAllUserItemWhichAreBooked(int userId,String state);

}
