package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingRequest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingRequestJsonTest {

    @Autowired
    private JacksonTester<BookingRequest> json;

    @Test
    void shouldSerializeBookingRequestToJson() throws Exception {
        LocalDateTime start = LocalDateTime.of(2026, 5, 5, 10, 0);
        LocalDateTime end = LocalDateTime.of(2026, 5, 6, 10, 0);

        BookingRequest request = new BookingRequest(10, start, end);

        JsonContent<BookingRequest> result = json.write(request);

        assertThat(result).extractingJsonPathNumberValue("$.itemId")
                .isEqualTo(10);

        assertThat(result).extractingJsonPathStringValue("$.start")
                .isEqualTo("2026-05-05T10:00:00");

        assertThat(result).extractingJsonPathStringValue("$.end")
                .isEqualTo("2026-05-06T10:00:00");
    }

    @Test
    void shouldDeserializeJsonToBookingRequest() throws Exception {
        String content = "{"
                + "\"itemId\": 10,"
                + "\"start\": \"2026-05-05T10:00:00\","
                + "\"end\": \"2026-05-06T10:00:00\""
                + "}";

        BookingRequest request = json.parseObject(content);

        assertThat(request.getItemId()).isEqualTo(10);
        assertThat(request.getStart()).isEqualTo(LocalDateTime.of(2026, 5, 5, 10, 0));
        assertThat(request.getEnd()).isEqualTo(LocalDateTime.of(2026, 5, 6, 10, 0));
    }
}