package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.ValidationException.EntityNotFoundException;
import ru.yandex.practicum.filmorate.ValidationException.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.Optional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FilmServiceImpl implements FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final MpaService mpaService;
    private final GenreService genreService;

    public FilmServiceImpl(
            @Qualifier("filmDbStorage") FilmStorage filmStorage,
            @Qualifier("userDbStorage") UserStorage userStorage,
            MpaService mpaService,
            GenreService genreService) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.mpaService = mpaService;
        this.genreService = genreService;
    }

    @Override
    public Film create(Film film) {

        validateMpa(film.getMpa());

        validateGenres(film.getGenres());
        return filmStorage.create(film);
    }

    @Override
    public Film get(long id) {
        return filmStorage.get(id)
                .orElseThrow(() -> new EntityNotFoundException("Фильм с ID " + id + " не найден"));
    }

    @Override
    public List<Film> getAll() {
        return filmStorage.getAll();
    }

    @Override
    public Film updateFilm(Film film) {
        if (film.getId() == null || film.getId() <= 0) {
            throw new ValidationException("ID фильма должен быть указан и положительным");
        }

        get(film.getId());

        validateMpa(film.getMpa());

        validateGenres(film.getGenres());
        return filmStorage.updateFilm(film);
    }

    @Override
    public void addLike(long filmId, long userId) throws Throwable {
        get(filmId);
        userStorage.getUser(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с ID " + userId + " не найден"));
        filmStorage.addLike(filmId, userId);
    }

    @Override
    public void removeLike(long filmId, long userId) throws Throwable {
        get(filmId);
        userStorage.getUser(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с ID " + userId + " не найден"));
        filmStorage.removeLike(filmId, userId);
    }

    @Override
    public List<Film> getPopularFilms(int count) {

        return filmStorage.getPopularFilms(count);
    }

    private void validateMpa(Mpa mpa) {
        if (mpa == null) {
            throw new ValidationException("MPA рейтинг обязателен");
        }
        if (mpa.getId() == null) {
            throw new ValidationException("ID MPA рейтинга обязателен");
        }


        mpaService.findById(mpa.getId());
    }


    private void validateGenres(List<Genre> genres) {
        if (genres != null && !genres.isEmpty()) {

            Set<Long> genreIds = genres.stream()
                    .map(Genre::getId)
                    .collect(Collectors.toSet());


            if (genreIds.contains(null)) {
                throw new ValidationException("ID жанра не может быть пустым");
            }


            List<Genre> existingGenres = genreService.findAllByIds(genreIds);


            if (existingGenres.size() != genreIds.size()) {
                Set<Long> existingIds = existingGenres.stream()
                        .map(Genre::getId)
                        .collect(Collectors.toSet());
                Set<Long> missingIds = genreIds.stream()
                        .filter(id -> !existingIds.contains(id))
                        .collect(Collectors.toSet());
                throw new EntityNotFoundException("Жанры с ID " + missingIds + " не найдены");
            }
        }
    }
}
