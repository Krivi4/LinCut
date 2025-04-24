package ru.Krivi4.LinCut.web.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.Krivi4.LinCut.dtos.UrlDTO;
import ru.Krivi4.LinCut.mappers.dto.UrlDTOMapper;
import ru.Krivi4.LinCut.mappers.view.UrlViewMapper;
import ru.Krivi4.LinCut.models.Url;
import ru.Krivi4.LinCut.services.BaseService;
import ru.Krivi4.LinCut.services.UrlService;
import ru.Krivi4.LinCut.views.UrlResponse;

import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class URLRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UrlService urlService;

    @MockBean
    private BaseService baseService;

    @MockBean
    private UrlDTOMapper urlDTOMapper;

    @MockBean
    private UrlViewMapper urlViewMapper;

    private UrlDTO urlDTO;
    private Url url;
    private UrlResponse urlResponse;

    /** Установка параметров модели, DTO, View длинной ссылки (перед каждым тестом)*/
    @BeforeEach
    void setUp() {
        urlDTO = new UrlDTO();
        urlDTO.setLongUrl("https://example.com");
        urlDTO.setHoursToLive(240);

        url = new Url();
        url.setId(1);
        url.setLongUrl("https://example.com");
        url.setCreatedDate(new Date());
        url.setExpiresDate(new Date(System.currentTimeMillis() + 240 * 3_600_000L));

        urlResponse = new UrlResponse();
        urlResponse.setShortUrl("localhost:-1/lincut/a");
        urlResponse.setValidHours(240);
    }

    /** Проверяет, что POST-запрос на /lincut создает короткую ссылку,
     возвращает статус 201 Created, заголовок Location (localhost:-1/lincut/a)
     и JSON-ответ с правильной короткой ссылкой и временем действия */
    @Test
    void createShortUrl_ShouldReturnCreated() throws Exception {
        when(urlDTOMapper.toEntity(any(UrlDTO.class))).thenReturn(url);
        when(urlService.save(any(Url.class))).thenReturn(url);
        when(baseService.encode(1)).thenReturn("a");
        when(urlViewMapper.toResponse(url)).thenReturn(urlResponse);

        mockMvc.perform(post("/lincut")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"longUrl\":\"https://example.com\",\"hoursToLive\":240}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "localhost:-1/lincut/a"))
                .andExpect(jsonPath("$['Короткая ссылка']").value("localhost:-1/lincut/a"))
                .andExpect(jsonPath("$['Время действия короткой ссылки(в часах)']").value(240));
    }

    /** Проверяет, что GET-запрос на /lincut/{shortUrl} возвращает статус 302 Found
     и перенаправляет на оригинальный URL, указанный в заголовке Location */
    @Test
    void getShortUrl_ShouldRedirect() throws Exception {
        when(urlService.getOriginalUrl("a")).thenReturn("https://example.com");

        mockMvc.perform(get("/lincut/a"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "https://example.com"));
    }
}