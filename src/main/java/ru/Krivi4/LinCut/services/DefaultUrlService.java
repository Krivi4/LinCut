package ru.Krivi4.LinCut.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.Krivi4.LinCut.models.Url;
import ru.Krivi4.LinCut.repositories.UrlRepository;

import javax.persistence.EntityNotFoundException;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class DefaultUrlService implements UrlService {
    private final UrlRepository urlRepository;
    private final BaseService baseService;


    /** Сохранение длинной ссылки*/
    @Override
    @Transactional
    public Url save(Url url) {
        return urlRepository.save(url);
    }

    /** Вывод зашифрованной длинной ссылки при вводе короткой ссылки*/
    @Override
    @Transactional
    public String getOriginalUrl(String shortUrl) {
        long id = baseService.decode(shortUrl);
        Url entity = urlRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Короткой ссылки " + shortUrl + " не существует:" ));

        Date now = new Date();
        if (entity.getExpiresDate() != null && entity.getExpiresDate().before(now)){
            urlRepository.delete(entity);
            throw new EntityNotFoundException("Срок действия короткой ссылки " + shortUrl +" истёк !");
        }

        return entity.getLongUrl();
    }
}

