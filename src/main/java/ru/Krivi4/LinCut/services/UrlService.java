package ru.Krivi4.LinCut.services;

import ru.Krivi4.LinCut.models.Url;

public interface UrlService {
    Url save(Url url);
    String getOriginalUrl(String shortUrl);
}
