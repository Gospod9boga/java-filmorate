package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Qualifier("inMemoryFilmStorage")
public class InMemoryFilmStorage implements FilmStorage {
    private Map<Long, Film> films = new HashMap<>();
    private long nextId = 1;

    @Override
    public Film create(Film film) {
        film.setId(getNextId());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Optional<Film> get(long id) {
        Film film = films.get(id);
        return Optional.ofNullable(film);
    }

    @Override
    public List<Film> getAll() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film updateFilm(Film film) {
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        return films.values().stream()
                .sorted(Comparator.comparingInt((Film f) -> f.getLikes().size()).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

    @Override
    public void addLike(long filmId, long userId) {
        Film film = films.get(filmId);
        if (film != null) {
            film.getLikes().add(userId);
        }
    }

    @Override
    public void removeLike(long filmId, long userId) {
        Film film = films.get(filmId);
        if (film != null) {
            film.getLikes().remove(userId);
        }
    }

    private long getNextId() {
        return nextId++;
    }
}
