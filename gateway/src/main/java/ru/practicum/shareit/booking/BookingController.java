package ru.practicum.shareit.booking;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.constants.HeaderConstants;

import java.util.Collection;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingClient bookingClient;

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingResponse> findBookingById(@PathVariable("bookingId") int bookingId,
                                                  @RequestHeader(HeaderConstants.SHARER_USER_ID) int userId) {
        return bookingClient.findBookingById(userId,bookingId);
    }

    @PostMapping
    public ResponseEntity<BookingResponse> save(@RequestHeader(HeaderConstants.SHARER_USER_ID) int userId,
                                                @Valid @RequestBody BookingRequest bookingRequest) {
        return bookingClient.createBooking(userId,bookingRequest);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingResponse> update(@RequestParam("approved") Boolean approved,
                                                  @PathVariable("bookingId") int bookingId,
                                                  @RequestHeader(HeaderConstants.SHARER_USER_ID) int userId) {
        return bookingClient.approveOrRejectBooking(userId,bookingId,approved);
    }

    @GetMapping
    public ResponseEntity<Collection<BookingResponse>> findUserBookings(@RequestHeader(HeaderConstants.SHARER_USER_ID) int userId,
                               @RequestParam(required = false) String state) {
        return bookingClient.findAllUserBookings(userId,state);
    }

    @GetMapping("/owner")
    public ResponseEntity<Collection<BookingResponse>> findAllUserItemWhichAreBooked(@RequestHeader(HeaderConstants.SHARER_USER_ID) int userId,
                                                                                     @RequestParam(required = false,defaultValue = "ALL") String state) {
        return bookingClient.findAllUserItemWhichAreBooked(userId,state);
    }

}
