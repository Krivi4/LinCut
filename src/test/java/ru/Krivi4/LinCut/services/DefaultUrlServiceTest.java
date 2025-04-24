package ru.Krivi4.LinCut.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.Krivi4.LinCut.models.Url;
import ru.Krivi4.LinCut.repositories.UrlRepository;

import javax.persistence.EntityNotFoundException;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DefaultUrlServiceTest {

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private BaseService baseService;

    @InjectMocks
    private DefaultUrlService urlService;

    private Url url;

    @BeforeEach
    void setUp() {
        url = new Url();
        url.setId(1);
        url.setLongUrl("https://example.com");
        url.setCreatedDate(new Date());
        url.setExpiresDate(new Date(System.currentTimeMillis() + 240 * 3_600_000L));
    }

    /** Проверяет, что метод save сохраняет объект Url в репозитории
     и возвращает сохраненный объект с правильным longUrl */
    @Test
    void save_ShouldSaveUrl() {
        when(urlRepository.save(url)).thenReturn(url);

        Url result = urlService.save(url);

        assertEquals("https://example.com", result.getLongUrl());
        verify(urlRepository).save(url);
    }

    /** Проверяет, что метод getOriginalUrl возвращает оригинальный URL
     для существующей и действующей короткой ссылки */
    @Test
    void getOriginalUrl_ShouldReturnLongUrl() {
        when(baseService.decode("a")).thenReturn(1L);
        when(urlRepository.findById(1L)).thenReturn(Optional.of(url));

        String result = urlService.getOriginalUrl("a");

        assertEquals("https://example.com", result);
    }

    /** Проверяет, что метод getOriginalUrl выбрасывает EntityNotFoundException,
     если короткая ссылка не существует в репозитории */
    @Test
    void getOriginalUrl_ShouldThrowNotFound_WhenUrlNotExists() {
        when(baseService.decode("a")).thenReturn(1L);
        when(urlRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> urlService.getOriginalUrl("a"));
    }

    /** Проверяет, что метод getOriginalUrl выбрасывает EntityNotFoundException
     и удаляет URL из репозитория, если срок действия ссылки истек */
    @Test
    void getOriginalUrl_ShouldThrowNotFound_WhenUrlExpired() {
        url.setExpiresDate(new Date(System.currentTimeMillis() - 1000)); // Expired
        when(baseService.decode("a")).thenReturn(1L);
        when(urlRepository.findById(1L)).thenReturn(Optional.of(url));

        assertThrows(EntityNotFoundException.class, () -> urlService.getOriginalUrl("a"));
        verify(urlRepository).delete(url);
    }
}