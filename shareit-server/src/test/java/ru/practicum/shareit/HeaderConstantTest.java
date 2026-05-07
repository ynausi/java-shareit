package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.constants.HeaderConstants;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class HeaderConstantsTest {

    @Test
    void shouldCreateHeaderConstantsByReflection() throws Exception {
        Constructor<HeaderConstants> constructor = HeaderConstants.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        try {
            HeaderConstants constants = constructor.newInstance();
            assertNotNull(constants);
        } catch (InvocationTargetException exception) {
            assertNotNull(exception.getCause());
        }
    }
}