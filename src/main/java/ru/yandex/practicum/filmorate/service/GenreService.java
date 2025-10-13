package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Collection;

public interface GenreService {
    List<Genre> findAll();

    Genre findById(Long id);

    List<Genre> findAllByIds(Collection<Long> ids);
}
