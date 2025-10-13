package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.ValidationException.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;

    private static final LocalDate EARLIEST_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        log.info("Create film: {}", film);
        validateReleaseDate(film.getReleaseDate());
        return filmService.create(film);
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable @Positive Long id) {
        log.info("Get film by id={}", id);

        Film film = filmService.get(id);

        return film;
    }

    @GetMapping
    public List<Film> getFilms() {
        log.info("Get films, count: {}", filmService.getAll().size());
        return filmService.getAll();
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.info("Update film: {}", film);
        validateReleaseDate(film.getReleaseDate());

        return filmService.updateFilm(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(
            @PathVariable @Positive Long id,
            @PathVariable @Positive Long userId) throws Throwable {
        log.info("User {} likes film {}", userId, id);

        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(
            @PathVariable @Positive Long id,
            @PathVariable @Positive Long userId) throws Throwable {
        log.info("User {} removes like from film {}", userId, id);

        filmService.removeLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(
            @RequestParam(defaultValue = "10") @Positive int count) {
        log.info("Get popular films, count: {}", count);
        return filmService.getPopularFilms(count);
    }

    private void validateReleaseDate(LocalDate releaseDate) {
        if (releaseDate.isBefore(EARLIEST_RELEASE_DATE)) {
            throw new ValidationException("Release date cannot be earlier than December 28, 1895");
        }
        if (releaseDate.isAfter(LocalDate.now())) {
            throw new ValidationException("Release date cannot be in the future");
        }
    }
}
