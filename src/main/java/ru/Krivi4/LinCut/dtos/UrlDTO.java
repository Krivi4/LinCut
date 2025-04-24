package ru.Krivi4.LinCut.dtos;


import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class UrlDTO {

    @NotNull(message = "Полная ссылка не может быть пустой")
    private String longUrl;

    @Min(value = 1, message = "Время жизни ссылки не может быть меньше 1 часа ")
    private Integer hoursToLive = 240;
}