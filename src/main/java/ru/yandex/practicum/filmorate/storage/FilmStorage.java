package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    Film create(Film film);

    Film get(long id);

    List<Film> getAll();

    Film updateFilm(Film film);
}
