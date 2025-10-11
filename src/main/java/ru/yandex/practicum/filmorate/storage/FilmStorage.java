package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    Film create(Film film);

    Optional get(long id);

    List<Film> getAll();

    Film updateFilm(Film film);

    List<Film> getPopularFilms(int count);

    void addLike(long filmId, long userId);

    void removeLike(long filmId, long userId);
}
