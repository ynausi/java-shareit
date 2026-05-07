package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;
import ru.practicum.ShareItServerApp;

import static org.mockito.Mockito.mockStatic;

class ShareItServerAppTest {

    @Test
    void shouldRunApplication() {
        String[] args = new String[]{};

        try (MockedStatic<SpringApplication> springApplication = mockStatic(SpringApplication.class)) {
            ShareItServerApp.main(args);

            springApplication.verify(
                    () -> SpringApplication.run(ShareItServerApp.class, args)
            );
        }
    }
}