package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.ValidationException.ValidationException;
import ru.yandex.practicum.filmorate.ValidationException.EntityNotFoundException;
import ru.yandex.practicum.filmorate.dto.ErrorResponse;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @Validated(User.OnCreate.class)
    public User createUser(@Valid @RequestBody User user) {
        log.info("Create user: {}", user);
        fixUserNameIfBlank(user);
        userService.createUser(user);
        return user;
    }

    @GetMapping
    public List<User> getUsers() {
        log.info("Get users");
        return userService.getUsers();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        log.info("Get user by id: {}", id);
        if (id == null) {
            throw new ValidationException("User id cannot be null");
        }
        User user = userService.getUser(id);
        if (user == null) {
            throw new EntityNotFoundException("User with id = " + id + " was not found");
        }
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        log.info("Update user: {}", user);
        if (user.getId() == null) {
            throw new ValidationException("User id cannot be null");
        }
        if (userService.getUser(user.getId()) == null) {
            throw new EntityNotFoundException("User with id = " + user.getId() + " was not found");
        }
        fixUserNameIfBlank(user);
        userService.updateUser(user);
        return user;
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("User {} adds friend {}", id, friendId);
        if (id == null || friendId == null) {
            throw new ValidationException("User id and friend id cannot be null");
        }
        if (userService.getUser(id) == null) {
            throw new EntityNotFoundException("User with id = " + id + " was not found");
        }
        if (userService.getUser(friendId) == null) {
            throw new EntityNotFoundException("User with id = " + friendId + " was not found");
        }
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("User {} removes friend {}", id, friendId);
        if (id == null || friendId == null) {
            throw new ValidationException("User id and friend id cannot be null");
        }
        if (userService.getUser(id) == null) {
            throw new EntityNotFoundException("User with id = " + id + " was not found");
        }
        if (userService.getUser(friendId) == null) {
            throw new EntityNotFoundException("User with id = " + friendId + " was not found");
        }
        userService.removeFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable Long id) {
        log.info("Get friends of user {}", id);
        if (id == null) {
            throw new ValidationException("User id cannot be null");
        }
        if (userService.getUser(id) == null) {
            throw new EntityNotFoundException("User with id = " + id + " was not found");
        }
        return userService.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable Long id, @PathVariable Long otherId) {
        log.info("Get common friends of users {} and {}", id, otherId);
        if (id == null || otherId == null) {
            throw new ValidationException("User ids cannot be null");
        }
        if (userService.getUser(id) == null) {
            throw new EntityNotFoundException("User with id = " + id + " was not found");
        }
        if (userService.getUser(otherId) == null) {
            throw new EntityNotFoundException("User with id = " + otherId + " was not found");
        }
        return userService.getCommonFriends(id, otherId);
    }

    private void fixUserNameIfBlank(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleNotFound(EntityNotFoundException e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleBadRequest(ValidationException e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}


