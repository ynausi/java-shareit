package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.CommentResponse;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CommentResponseJsonTest {

    @Autowired
    private JacksonTester<CommentResponse> json;

    @Test
    void shouldSerializeCommentResponseToJson() throws Exception {
        LocalDateTime created = LocalDateTime.of(2026, 5, 4, 22, 36, 46);

        CommentResponse response = new CommentResponse();
        response.setId(1);
        response.setText("Отличная, пользовался");
        response.setAuthorName("Ynausi");
        response.setCreated(created);

        JsonContent<CommentResponse> result = json.write(response);

        assertThat(result).extractingJsonPathNumberValue("$.id")
                .isEqualTo(1);

        assertThat(result).extractingJsonPathStringValue("$.text")
                .isEqualTo("Отличная, пользовался");

        assertThat(result).extractingJsonPathStringValue("$.authorName")
                .isEqualTo("Ynausi");

        assertThat(result).extractingJsonPathStringValue("$.created")
                .isEqualTo("2026-05-04T22:36:46");
    }
}
