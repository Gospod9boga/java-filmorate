package ru.yandex.practicum.filmorate.validator;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.ValidationException.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

@Service
public class FilmValidator {

    private static final int MAX_DESCRIPTION_LENGTH = 200;
    private static final LocalDate EARLIEST_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    public static void validate(Film film) {
        validateName(film.getName());
        validateDescription(film.getDescription());
        validateReleaseDate(film.getReleaseDate());
        validateDuration(film.getDuration());
    }

    private static void validateName(String name) {
        if (StringUtils.isBlank(name)) {
            throw new ValidationException("Empty film name");
        }
    }

    private static void validateDescription(String description) {
        if (StringUtils.isBlank(description)) {
            throw new ValidationException("Empty film description");
        }
        if (description.length() > MAX_DESCRIPTION_LENGTH) {
            throw new ValidationException("Description length must be <= " + MAX_DESCRIPTION_LENGTH + " characters");
        }
    }

    private static void validateReleaseDate(LocalDate releaseDate) {
        if (releaseDate == null) {
            throw new ValidationException("Release date cannot be null");
        }
        if (releaseDate.isBefore(EARLIEST_RELEASE_DATE)) {
            throw new ValidationException("Release date cannot be earlier than December 28, 1895");
        }
    }

    private static void validateDuration(int duration) {
        if (duration <= 0) {
            throw new ValidationException("Duration must be positive");
        }
    }
}

