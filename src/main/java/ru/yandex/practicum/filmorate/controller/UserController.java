package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.ValidationException.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private Map<Long, User> users = new HashMap<>();
    private long nextId = 1;

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        log.info("Create user: {}", user);

        fixUserNameIfBlank(user);

        user.setId(getNextId());
        users.put(user.getId(), user);
        return user;
    }

    @GetMapping
    public List<User> getUsers() {
        log.info("Get users");
        return new ArrayList<>(users.values());
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        log.info("Update user: {}", user);

        if (user.getId() == null || !users.containsKey(user.getId())) {
            throw new ValidationException("User with id = " + user.getId() + " was not found");
        }

        fixUserNameIfBlank(user);

        users.put(user.getId(), user);
        return user;
    }

    private void fixUserNameIfBlank(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }

    private long getNextId() {
        return nextId++;
    }
}
