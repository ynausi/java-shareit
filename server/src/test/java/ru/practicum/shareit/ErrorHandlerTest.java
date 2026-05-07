package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.practicum.shareit.exceptions.ErrorHandler;
import ru.practicum.shareit.exceptions.ErrorResponse;
import ru.practicum.shareit.exceptions.InternalServerException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.UserEmailAlreadyExistsException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ErrorHandlerTest {

    private final ErrorHandler errorHandler = new ErrorHandler();

    @Test
    void shouldHandleNotFoundException() {
        ErrorResponse response = errorHandler.handleNotFound(
                new NotFoundException("not found")
        );

        assertEquals("not found", response.getError());
    }

    @Test
    void shouldHandleIllegalArgumentException() {
        ErrorResponse response = errorHandler.handleIllegalArgument(
                new IllegalArgumentException("bad request")
        );

        assertEquals("bad request", response.getError());
    }

    @Test
    void shouldHandleCommonException() {
        ErrorResponse response = errorHandler.handleException(
                new RuntimeException("some error")
        );

        assertEquals("Внутренняя ошибка сервера", response.getError());
    }

    @Test
    void shouldHandleValidationException() {
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError(
                "object",
                "name",
                "name must not be blank"
        );

        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldError()).thenReturn(fieldError);

        ErrorResponse response = errorHandler.handleValidation(exception);

        assertEquals("name must not be blank", response.getError());
    }

    @Test
    void shouldCreateCustomExceptions() {
        InternalServerException internal = new InternalServerException("internal");
        NotFoundException notFound = new NotFoundException("not found");
        UserEmailAlreadyExistsException emailExists =
                new UserEmailAlreadyExistsException("email exists");

        assertEquals("internal", internal.getMessage());
        assertEquals("not found", notFound.getMessage());
        assertEquals("email exists", emailExists.getMessage());
    }
}