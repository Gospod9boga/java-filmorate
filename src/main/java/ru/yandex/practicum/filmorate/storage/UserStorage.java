package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {

    User createUser(User user);

    List<User> getUsers();

    User updateUser(User user);

    User getUser(Long id);

}
