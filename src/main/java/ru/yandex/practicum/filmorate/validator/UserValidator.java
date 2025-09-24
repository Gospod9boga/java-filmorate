package ru.yandex.practicum.filmorate.validator;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.ValidationException.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

@Service
public class UserValidator {

    public static void validate(User user) {
        validateEmail(user.getEmail());
        validateLogin(user.getLogin());
        validateName(user);
        validateBirthday(user.getBirthday());
    }

    private static void validateEmail(String email) {
        if (StringUtils.isBlank(email)) {
            throw new ValidationException("Email cannot be empty");
        }
        if (!email.contains("@")) {
            throw new ValidationException("Email must contain '@'");
        }
    }

    private static void validateLogin(String login) {
        if (StringUtils.isBlank(login)) {
            throw new ValidationException("Login cannot be empty");
        }
        if (login.contains(" ")) {
            throw new ValidationException("Login cannot contain spaces");
        }
    }

    private static void validateName(User user) {
        if (StringUtils.isBlank(user.getName())) {
            user.setName(user.getLogin());
        }
    }

    private static void validateBirthday(LocalDate birthday) {
        if (birthday == null) {
            throw new ValidationException("Birthday cannot be null");
        }
        if (birthday.isAfter(LocalDate.now())) {
            throw new ValidationException("Birthday cannot be in the future");
        }
    }
}

