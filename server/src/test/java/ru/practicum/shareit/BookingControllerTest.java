package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.dto.BookerResponse;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.dto.ItemShortResponse;
import ru.practicum.shareit.booking.service.BookingService;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;


@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

    @Test
    void shouldCreateBooking() throws Exception {
        int userId = 1;
        int itemId = 10;
        int bookingId = 100;

        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        BookingRequest request = new BookingRequest(itemId, start, end);

        BookerResponse booker = new BookerResponse();
        booker.setId(userId);

        ItemShortResponse item = new ItemShortResponse();
        item.setId(itemId);
        item.setName("Дрель");

        BookingResponse response = new BookingResponse();
        response.setId(bookingId);
        response.setStart(start);
        response.setEnd(end);
        response.setBooker(booker);
        response.setItem(item);

        Mockito.when(bookingService.save(
                Mockito.any(BookingRequest.class),
                Mockito.eq(userId)
        )).thenReturn(response);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(bookingId))
                .andExpect(jsonPath("$.booker.id").value(userId))
                .andExpect(jsonPath("$.item.id").value(itemId))
                .andExpect(jsonPath("$.item.name").value("Дрель"));

        Mockito.verify(bookingService).save(
                Mockito.any(BookingRequest.class),
                Mockito.eq(userId)
        );
    }

    @Test
    void shouldFindBookingById() throws Exception {
        int userId = 1;
        int bookingId = 100;
        int itemId = 10;

        BookerResponse booker = new BookerResponse();
        booker.setId(userId);

        ItemShortResponse item = new ItemShortResponse();
        item.setId(itemId);
        item.setName("Дрель");

        BookingResponse response = new BookingResponse();
        response.setId(bookingId);
        response.setBooker(booker);
        response.setItem(item);

        Mockito.when(bookingService.findBookingById(bookingId, userId))
                .thenReturn(response);

        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingId))
                .andExpect(jsonPath("$.booker.id").value(userId))
                .andExpect(jsonPath("$.item.id").value(itemId))
                .andExpect(jsonPath("$.item.name").value("Дрель"));

        Mockito.verify(bookingService).findBookingById(bookingId, userId);
    }

    @Test
    void shouldUpdateBookingApprovedStatus() throws Exception {
        int userId = 1;
        int bookingId = 100;
        boolean approved = true;

        BookingResponse response = new BookingResponse();
        response.setId(bookingId);

        Mockito.when(bookingService.update(bookingId, approved, userId))
                .thenReturn(response);

        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId)
                        .param("approved", String.valueOf(approved))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingId));

        Mockito.verify(bookingService).update(bookingId, approved, userId);
    }

    @Test
    void shouldFindUserBookings() throws Exception {
        int userId = 1;

        BookingResponse response1 = new BookingResponse();
        response1.setId(100);

        BookingResponse response2 = new BookingResponse();
        response2.setId(101);

        Collection<BookingResponse> responses = List.of(response1, response2);

        Mockito.when(bookingService.findAllUserBookings(userId, "ALL"))
                .thenReturn(responses);

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", "ALL")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(100))
                .andExpect(jsonPath("$[1].id").value(101));

        Mockito.verify(bookingService).findAllUserBookings(userId, "ALL");
    }

    @Test
    void shouldFindOwnerBookings() throws Exception {
        int userId = 1;

        BookingResponse response1 = new BookingResponse();
        response1.setId(100);

        BookingResponse response2 = new BookingResponse();
        response2.setId(101);

        Collection<BookingResponse> responses = List.of(response1, response2);

        Mockito.when(bookingService.findAllUserItemWhichAreBooked(userId, "ALL"))
                .thenReturn(responses);

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", "ALL")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(100))
                .andExpect(jsonPath("$[1].id").value(101));

        Mockito.verify(bookingService).findAllUserItemWhichAreBooked(userId, "ALL");
    }
}
