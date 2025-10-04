package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.ValidationException.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private static final LocalDate EARLIEST_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    private Map<Long, Film> films = new HashMap<>();
    private long nextId = 1;

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        log.info("Create film: {}", film);

        validateReleaseDate(film.getReleaseDate());
        film.setId(getNextId());
        films.put(film.getId(), film);
        return film;
    }

    @GetMapping("/{filmId}")
    public Film getFilmById(@PathVariable long filmId) {
        log.info("Get film by id={}", filmId);

        Film film = films.get(filmId);
        if (film == null) {
            throw new ValidationException("Film with id = " + filmId + " was not found");
        }
        return film;
    }

    @GetMapping
    public List<Film> getFilms() {
        log.info("Get films, count: {}", films.size());
        return new ArrayList<>(films.values());
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.info("Update film: {}", film);

        validateReleaseDate(film.getReleaseDate());

        if (film.getId() == null || !films.containsKey(film.getId())) {
            throw new ValidationException("Film with id = " + film.getId() + " was not found");
        }

        films.put(film.getId(), film);
        return film;
    }

    private long getNextId() {
        return nextId++;
    }

    private void validateReleaseDate(LocalDate releaseDate) {
        if (releaseDate.isBefore(EARLIEST_RELEASE_DATE)) {
            throw new ValidationException("Release date cannot be earlier than December 28, 1895");
        }
    }
}