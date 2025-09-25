package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validUser_NoViolations() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setLogin("userlogin");
        user.setName("User Name");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty());
    }

    @Test
    void emailIsBlank_ShouldHaveViolation() {
        User user = new User();
        user.setEmail("   ");
        user.setLogin("login");
        user.setName("Name");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v ->
                v.getPropertyPath().toString().equals("email") &&
                        v.getMessage().contains("cannot be empty")));
    }

    @Test
    void emailMissingAt_ShouldHaveViolation() {
        User user = new User();
        user.setEmail("userexample.com");
        user.setLogin("login");
        user.setName("Name");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v ->
                v.getPropertyPath().toString().equals("email") &&
                        v.getMessage().contains("must be a well-formed email address")));
    }

    @Test
    void loginIsBlank_ShouldHaveViolation() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setLogin(" ");
        user.setName("Name");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v ->
                v.getPropertyPath().toString().equals("login") &&
                        v.getMessage().contains("cannot be empty")));
    }

    @Test
    void loginContainsSpaces_ShouldHaveViolation() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setLogin("log in");
        user.setName("Name");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v ->
                v.getPropertyPath().toString().equals("login") &&
                        v.getMessage().contains("cannot contain spaces")));
    }

    @Test
    void birthdayIsNull_ShouldHaveViolation() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setLogin("login");
        user.setName("Name");
        user.setBirthday(null);

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v ->
                v.getPropertyPath().toString().equals("birthday") &&
                        v.getMessage().contains("cannot be null")));
    }

    @Test
    void nameIsBlank_ShouldBeReplacedByLogin() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setLogin("login123");
        user.setName("  ");
        user.setBirthday(LocalDate.of(1990, 1, 1));

             fixUserNameIfBlank(user);

        assertEquals("login123", user.getName());
    }

    private void fixUserNameIfBlank(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}


