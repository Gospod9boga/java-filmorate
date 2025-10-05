package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.ValidationException.EntityNotFoundException;
import ru.yandex.practicum.filmorate.ValidationException.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmServiceImpl implements FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public FilmServiceImpl(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    @Override
    public Film create(Film film) {
        return filmStorage.create(film);
    }

    @Override
    public Film get(long id) {
        return filmStorage.get(id);
    }

    @Override
    public List<Film> getAll() {
        return filmStorage.getAll();
    }

    @Override
    public Film updateFilm(Film film) {

        if (this.get(film.getId()) == null) {
            throw new EntityNotFoundException("Film with id = " + film.getId() + " was not found");
        }
        return filmStorage.updateFilm(film);
    }

    @Override
    public void addLike(long filmId, long userId) {
        Film film = getFilm(filmId);
        getUser(userId);

        if (film.getLikes().contains(userId)) {
            throw new ValidationException("Пользователь уже поставил лайк этому фильму");
        }

        film.getLikes().add(userId);
        filmStorage.updateFilm(film);
    }

    @Override
    public void removeLike(long filmId, long userId) {
        Film film = getFilm(filmId);
        getUser(userId);

        if (!film.getLikes().contains(userId)) {
            throw new ValidationException("Пользователь не ставил лайк этому фильму");
        }

        film.getLikes().remove(userId);
        filmStorage.updateFilm(film);
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        return filmStorage.getAll().stream()
                .sorted(Comparator.comparingInt((Film f) -> f.getLikes().size()).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

    private Film getFilm(long filmId) {
        Film film = filmStorage.get(filmId);
        if (film == null) {
            throw new EntityNotFoundException("Фильм с ID " + filmId + " не найден");
        }
        return film;
    }

    private void getUser(long userId) {
        if (userStorage.getUser(userId) == null) {
            throw new EntityNotFoundException("Пользователь с ID " + userId + " не найден");
        }
    }
}



