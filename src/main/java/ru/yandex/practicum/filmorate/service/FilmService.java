package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

@Service
public interface FilmService {
    Film create(Film film);

    Film get(long id);

    List<Film> getAll();

    Film updateFilm(Film film);

    void addLike(long filmId, long userId);

    void removeLike(long filmId, long userId);

    List<Film> getPopularFilms(int count);
}

