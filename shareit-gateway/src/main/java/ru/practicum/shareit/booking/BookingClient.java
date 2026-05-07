package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.client.BaseClient;

@Component
public class BookingClient extends BaseClient {
    public BookingClient(
            RestTemplate restTemplate,
            @Value("${shareit-server.url}") String serverUrl
    ) {
        super(restTemplate, serverUrl);
    }

    public ResponseEntity<Object> createBooking(Integer userId, BookingRequest request) {
        return post("/bookings",userId,request);
    }

    public ResponseEntity<Object> approveOrRejectBooking(Integer userId,Integer bookingId, Boolean approved) {
        return patch("/bookings/" + bookingId + "?approved=" + approved,userId,null);
    }

    public ResponseEntity<Object> findBookingById(Integer userId,Integer bookingId) {
        return get("/bookings/" + bookingId,userId);
    }

    public ResponseEntity<Object> findAllUserBookings(Integer userId, String state) {
        if (state == null || state.isBlank()) {
            return get("/bookings", userId);
        }
        return get("/bookings?state=" + state,userId);
    }

    public ResponseEntity<Object> findAllUserItemWhichAreBooked(Integer userId,String state) {
        if (state == null || state.isBlank()) {
            return get("/bookings/owner", userId);
        }
        return get("/bookings/owner?state=" + state,userId);
    }
}
