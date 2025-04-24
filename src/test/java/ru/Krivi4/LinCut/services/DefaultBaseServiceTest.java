package ru.Krivi4.LinCut.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DefaultBaseServiceTest {

    private final DefaultBaseService baseService = new DefaultBaseService();

    /** Проверяет, что метод encode преобразует число в строку,
     состоящую из разрешенных символов (a-z, A-Z, 0-9) */
    @Test
    void encode_ShouldEncodeNumber() {
        String result = baseService.encode(123);

        assertNotNull(result);
        assertTrue(result.matches("[a-zA-Z0-9]+"));
    }

    /** Проверяет, что метод encode возвращает символ 'a' для входного значения 0 */
    @Test
    void encode_ShouldReturnSingleCharacterForZero() {
        String result = baseService.encode(0);

        assertEquals("a", result);
    }

    /** Проверяет, что метод decode корректно декодирует строку,
     полученную из encode, обратно в исходное число */
    @Test
    void decode_ShouldDecodeString() {
        String encoded = baseService.encode(123);
        long result = baseService.decode(encoded);

        assertEquals(123, result);
    }

    /** Проверяет, что метод decode возвращает 0 для строки 'a' */
    @Test
    void decode_ShouldReturnZeroForSingleCharacter() {
        long result = baseService.decode("a");

        assertEquals(0, result);
    }
}