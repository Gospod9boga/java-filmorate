package ru.yandex.practicum.filmorate.ValidationException;

public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}
