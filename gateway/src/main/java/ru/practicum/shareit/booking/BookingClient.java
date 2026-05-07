package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.client.BaseClient;

import java.util.Collection;

@Component
public class BookingClient extends BaseClient {
    public BookingClient(
            RestTemplate restTemplate,
            @Value("${shareit-server.url}") String serverUrl) {
        super(restTemplate, serverUrl);
    }

    public ResponseEntity<BookingResponse> createBooking(Integer userId, BookingRequest request) {
        return post("/bookings",userId,request,BookingResponse.class);
    }

    public ResponseEntity<BookingResponse> approveOrRejectBooking(Integer userId,Integer bookingId, Boolean approved) {
        return patch("/bookings/" + bookingId + "?approved=" + approved,userId,null,BookingResponse.class);
    }

    public ResponseEntity<BookingResponse> findBookingById(Integer userId,Integer bookingId) {
        return get("/bookings/" + bookingId,userId,BookingResponse.class);
    }

    public ResponseEntity<Collection<BookingResponse>> findAllUserBookings(Integer userId, String state) {
        if (state == null || state.isBlank()) {
            return get("/bookings", userId,new ParameterizedTypeReference<Collection<BookingResponse>>() {});
        }
        return get("/bookings?state=" + state,userId,new ParameterizedTypeReference<Collection<BookingResponse>>() {});
    }

    public ResponseEntity<Collection<BookingResponse>> findAllUserItemWhichAreBooked(Integer userId,String state) {
        if (state == null || state.isBlank()) {
            return get("/bookings/owner", userId,new ParameterizedTypeReference<Collection<BookingResponse>>() {});
        }
        return get("/bookings/owner?state=" + state,userId,new ParameterizedTypeReference<Collection<BookingResponse>>() {});
    }
}
