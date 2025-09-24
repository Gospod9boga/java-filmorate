package ru.yandex.practicum.filmorate.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.ValidationException.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validator.UserValidator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private Map<Long, User> users = new HashMap<>();
    private long nextId = 1;

    @PostMapping
    public User createUser(@RequestBody User user) {
        log.info("Create user: = {}", user);
        UserValidator.validate(user);
        user.setId(getNextId());
        users.put(user.getId(), user);
        return user;
    }

    @GetMapping
    public List<User> getUsers() {
        log.info("Get user ");
        return new ArrayList<>(users.values());
    }

    @PutMapping
    public User updateUser(@RequestBody User user) {
        log.info("Update user:  {}", user);
        UserValidator.validate(user);
        if (user.getId() == null || !users.containsKey(user.getId())) {
            throw new ValidationException("User with id = " + user.getId() + " was not found");
        }
        users.put(user.getId(), user);
        return user;
    }


    private long getNextId() {
        return nextId++;
    }
}
