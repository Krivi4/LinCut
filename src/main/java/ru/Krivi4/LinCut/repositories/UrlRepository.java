package ru.Krivi4.LinCut.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.Krivi4.LinCut.models.Url;

@Repository
public interface UrlRepository extends JpaRepository<Url, Long> {

}
