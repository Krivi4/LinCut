package ru.Krivi4.LinCut.mappers.view;

import org.mapstruct.Mapper;
import ru.Krivi4.LinCut.models.Url;
import ru.Krivi4.LinCut.views.UrlResponse;

@Mapper(componentModel = "spring")
public interface UrlViewMapper {

    UrlResponse  toResponse(Url url);
}
