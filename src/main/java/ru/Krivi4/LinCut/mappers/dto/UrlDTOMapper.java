package ru.Krivi4.LinCut.mappers.dto;

import org.mapstruct.Mapper;
import ru.Krivi4.LinCut.dtos.UrlDTO;
import ru.Krivi4.LinCut.models.Url;

import java.util.Date;

@Mapper(componentModel = "spring")
public interface UrlDTOMapper {

    default Url toEntity(UrlDTO dto) {
        Url url = new Url();
        url.setLongUrl(dto.getLongUrl());
        Date now = new Date();
        url.setCreatedDate(now);

        // если hoursToLive не указан — 240 часов, иначе -  то, что пришло
        int hours = dto.getHoursToLive() == null ? 240 : dto.getHoursToLive();
        url.setExpiresDate(new Date(now.getTime() + hours * 3_600_000L));

        return url;
    }
}