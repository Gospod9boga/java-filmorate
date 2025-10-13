package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.ValidationException.ValidationException;
import ru.yandex.practicum.filmorate.ValidationException.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;

@Slf4j
@Validated  
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
    public User getUserById(@PathVariable @Positive Long id) {
        log.info("Get user by id: {}", id);


        User user = userService.getUser(id);
        if (user == null) {
            throw new EntityNotFoundException("User with id = " + id + " was not found");
        }
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        log.info("Update user: {}", user);

        if (userService.getUser(user.getId()) == null) {
            throw new EntityNotFoundException("User with id = " + user.getId() + " was not found");
        }
        fixUserNameIfBlank(user);
        userService.updateUser(user);
        return user;
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(
            @PathVariable @Positive Long id,
            @PathVariable @Positive Long friendId) {
        log.info("User {} adds friend {}", id, friendId);


        if (userService.getUser(id) == null) {
            throw new EntityNotFoundException("User with id = " + id + " was not found");
        }
        if (userService.getUser(friendId) == null) {
            throw new EntityNotFoundException("User with id = " + friendId + " was not found");
        }
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(
            @PathVariable @Positive Long id,
            @PathVariable @Positive Long friendId) {
        log.info("User {} removes friend {}", id, friendId);


        if (userService.getUser(id) == null) {
            throw new EntityNotFoundException("User with id = " + id + " was not found");
        }
        if (userService.getUser(friendId) == null) {
            throw new EntityNotFoundException("User with id = " + friendId + " was not found");
        }
        userService.removeFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable @Positive Long id) {
        log.info("Get friends of user {}", id);


        if (userService.getUser(id) == null) {
            throw new EntityNotFoundException("User with id = " + id + " was not found");
        }
        return userService.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(
            @PathVariable @Positive Long id,
            @PathVariable @Positive Long otherId) {
        log.info("Get common friends of users {} and {}", id, otherId);


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
}
