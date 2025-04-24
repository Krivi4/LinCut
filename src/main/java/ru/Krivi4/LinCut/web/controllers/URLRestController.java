package ru.Krivi4.LinCut.web.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.Krivi4.LinCut.dtos.UrlDTO;
import ru.Krivi4.LinCut.mappers.dto.UrlDTOMapper;
import ru.Krivi4.LinCut.mappers.view.UrlViewMapper;
import ru.Krivi4.LinCut.models.Url;
import ru.Krivi4.LinCut.services.BaseService;
import ru.Krivi4.LinCut.services.UrlService;

import org.springframework.cache.annotation.Cacheable;
import ru.Krivi4.LinCut.views.UrlResponse;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/lincut")
@RequiredArgsConstructor
public class URLRestController {

    private final UrlService urlService;
    private final BaseService baseService;
    private final UrlDTOMapper urlDTOMapper;
    private final UrlViewMapper urlViewMapper;


    /** Создание короткой ссылки при вводе длинной*/
    @PostMapping
    public ResponseEntity<UrlResponse> createShortUrl(@Valid @RequestBody UrlDTO dto) {
        Url saved = urlService.save(urlDTOMapper.toEntity(dto));

        String key = baseService.encode(saved.getId());

        URI location = URI.create(ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .build()
                .getHost() + ":"
                + ServletUriComponentsBuilder
                    .fromCurrentContextPath()
                    .build()
                    .getPort()
                + "/lincut/" + key);


        UrlResponse resp = urlViewMapper.toResponse(saved);
        resp.setShortUrl(location.toString());

        resp.setValidHours(dto.getHoursToLive());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .location(location)
                .body(resp);
    }

    /** Вывод зашифрованной длинной ссылки при вводе короткой ссылки*/
    @GetMapping(value = "{shortUrl}")
    @Cacheable(value = "urls", key = "#shortUrl", sync = true)
    public ResponseEntity<Void> getShortUrl(@PathVariable String shortUrl){
            String url = urlService.getOriginalUrl(shortUrl);
            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create(url))
                    .build();
        }



}
