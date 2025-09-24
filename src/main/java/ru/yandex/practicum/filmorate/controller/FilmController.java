package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.ValidationException.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validator.FilmValidator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private Map<Long, Film> films;
    private long nextId;

    public FilmController(FilmValidator validator) {

        this.films = new HashMap<>();
        this.nextId = 1;
    }

    @PostMapping
    public Film createFilm(@RequestBody Film film) {
        log.info("Create film: ={}", film);
        FilmValidator.validate(film);
        film.setId(getNextId());
        films.put(film.getId(), film);
        return film;
    }


    @GetMapping("/{filmId}")
    public Film getFilmById(@PathVariable long filmId) {
        log.info("Get film by id={}", filmId);

        Film film = films.get(filmId);

        if (film == null) {
            throw new ValidationException("Film with i = " + filmId + "was not found");
        }
        return film;
    }

    @GetMapping
    public List<Film> getFilms() {
        log.info("Get film ");
        return new ArrayList<>(films.values());
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        log.info("Update film: {}", film);
        FilmValidator.validate(film);

        if (film.getId() == null || !films.containsKey(film.getId())) {
            throw new ValidationException("Film with id = " + film.getId() + " was not found");
        }

        films.put(film.getId(), film);
        return film;
    }


    private long getNextId() {
        return nextId++;
    }
}
