package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.ValidationException.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validator.FilmValidator;

import java.time.LocalDate;

public class FilmValidatorTest {

    @Test
    void whenFilmNameIsEmptyThenThrowException() {
        Film film = new Film(null, "", "description");
        Assertions.assertThrows(ValidationException.class, () -> FilmValidator.validate(film));
    }

    @Test
    void whenFilmNameIsNullThenThrowException() {
        Film film = new Film(null, null, "description");
        Assertions.assertThrows(ValidationException.class, () -> FilmValidator.validate(film));
    }

    @Test
    void whenFilmDescriptionIsEmptyThenThrowException() {
        Film film = new Film("Name", " ", null);
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(100);
        Assertions.assertThrows(ValidationException.class, () -> FilmValidator.validate(film));
    }

    @Test
    void whenFilmDescriptionIsTooLongThenThrowException() {
        String longDescription = "a".repeat(201);
        Film film = new Film("Name", longDescription, null);
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(100);
        Assertions.assertThrows(ValidationException.class, () -> FilmValidator.validate(film));
    }

    @Test
    void whenReleaseDateIsNullThenThrowException() {
        Film film = new Film("Name", "Description", null);
        film.setReleaseDate(null);
        film.setDuration(100);
        Assertions.assertThrows(ValidationException.class, () -> FilmValidator.validate(film));
    }

    @Test
    void whenReleaseDateBeforeEarliestThenThrowException() {
        Film film = new Film("Name", "Description", null);
        film.setReleaseDate(LocalDate.of(1895, 12, 27)); // на день раньше
        film.setDuration(100);
        Assertions.assertThrows(ValidationException.class, () -> FilmValidator.validate(film));
    }


    @Test
    void whenDurationIsZeroThenThrowException() {
        Film film = new Film("Name", "Description", null);
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(0);
        Assertions.assertThrows(ValidationException.class, () -> FilmValidator.validate(film));
    }

    @Test
    void whenDurationIsNegativeThenThrowException() {
        Film film = new Film("Name", "Description", null);
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(-10);
        Assertions.assertThrows(ValidationException.class, () -> FilmValidator.validate(film));
    }


}

