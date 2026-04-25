package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.service.BookingService;

import java.net.URI;
import java.util.Collection;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingResponse> findBookingById(@PathVariable("bookingId") int bookingId,
                                                           @RequestHeader("X-Sharer-User-Id") int userId) {
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
                .body(bookingService.findBookingById(bookingId,userId));
    }

    @PostMapping
    public ResponseEntity<BookingResponse> save(@RequestHeader("X-Sharer-User-Id") int userId,
                                                @Valid @RequestBody BookingRequest bookingRequest) {
        BookingResponse created = bookingService.save(bookingRequest,userId);
        return ResponseEntity.created(URI.create("/" + created.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .body(created);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingResponse> update(@Param("approved") Boolean approved,
                                                  @PathVariable("bookingId") int bookingId,
                                                  @RequestHeader("X-Sharer-User-Id") int userId) {
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
                .body(bookingService.update(bookingId,approved,userId));
    }

    @GetMapping
    public ResponseEntity<Collection<BookingResponse>> findUserBookings(@RequestHeader("X-Sharer-User-Id") int userId,
                               @RequestParam(required = false,defaultValue = "ALL") String state) {
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
                .body(bookingService.findAllUserBookings(userId,state));
    }

    @GetMapping("/owner")
    public ResponseEntity<Collection<BookingResponse>> findAllUserItemWhichAreBooked(@RequestHeader("X-Sharer-User-Id") int userId,
                                @RequestParam(required = false,defaultValue = "ALL") String state) {
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
                .body(bookingService.findAllUserItemWhichAreBooked(userId,state));
    }

}
