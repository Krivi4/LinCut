package ru.Krivi4.LinCut.views;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UrlResponse {

    @JsonProperty("Короткая ссылка")
    private String shortUrl;

    @JsonProperty("Время действия короткой ссылки(в часах)")
    private int validHours;
}
