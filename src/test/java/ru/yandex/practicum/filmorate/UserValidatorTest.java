package ru.yandex.practicum.filmorate;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.ValidationException.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validator.UserValidator;

import java.time.LocalDate;

class UserValidatorTest {

    @Test
    void validate_ValidUser_NoException() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setLogin("userlogin");
        user.setName("User Name");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        Assertions.assertDoesNotThrow(() -> UserValidator.validate(user));
    }

    @Test
    void validate_EmailIsBlank_ThrowsException() {
        User user = new User();
        user.setEmail("   ");
        user.setLogin("login");
        user.setName("Name");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        ValidationException ex = Assertions.assertThrows(ValidationException.class,
                () -> UserValidator.validate(user));
        Assertions.assertEquals("Email cannot be empty", ex.getMessage());
    }

    @Test
    void validate_EmailMissingAt_ThrowsException() {
        User user = new User();
        user.setEmail("userexample.com");
        user.setLogin("login");
        user.setName("Name");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        ValidationException ex = Assertions.assertThrows(ValidationException.class,
                () -> UserValidator.validate(user));
        Assertions.assertEquals("Email must contain '@'", ex.getMessage());
    }

    @Test
    void validate_LoginIsBlank_ThrowsException() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setLogin(" ");
        user.setName("Name");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        ValidationException ex = Assertions.assertThrows(ValidationException.class,
                () -> UserValidator.validate(user));
        Assertions.assertEquals("Login cannot be empty", ex.getMessage());
    }

    @Test
    void validate_LoginContainsSpaces_ThrowsException() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setLogin("log in");
        user.setName("Name");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        ValidationException ex = Assertions.assertThrows(ValidationException.class,
                () -> UserValidator.validate(user));
        Assertions.assertEquals("Login cannot contain spaces", ex.getMessage());
    }

    @Test
    void validate_NameIsBlank_NameReplacedByLogin() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setLogin("login123");
        user.setName("  ");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        UserValidator.validate(user);

        Assertions.assertEquals("login123", user.getName());
    }

    @Test
    void validate_BirthdayIsNull_ThrowsException() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setLogin("login");
        user.setName("Name");
        user.setBirthday(null);

        ValidationException ex = Assertions.assertThrows(ValidationException.class,
                () -> UserValidator.validate(user));
        Assertions.assertEquals("Birthday cannot be null", ex.getMessage());
    }

    @Test
    void validate_BirthdayInFuture_ThrowsException() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setLogin("login");
        user.setName("Name");
        user.setBirthday(LocalDate.now().plusDays(1));

        ValidationException ex = Assertions.assertThrows(ValidationException.class,
                () -> UserValidator.validate(user));
        Assertions.assertEquals("Birthday cannot be in the future", ex.getMessage());
    }
}

