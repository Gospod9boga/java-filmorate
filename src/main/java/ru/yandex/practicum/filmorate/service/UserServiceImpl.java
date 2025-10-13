package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.ValidationException.EntityNotFoundException;
import ru.yandex.practicum.filmorate.ValidationException.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.Optional;


@Service
public class UserServiceImpl implements UserService {

    private final UserStorage storage;

    public UserServiceImpl(@Qualifier("userDbStorage") UserStorage storage) {
        this.storage = storage;
    }

    @Override
    public User createUser(User user) {
        return storage.createUser(user);
    }

    @Override
    public List<User> getUsers() {
        return storage.getUsers();
    }

    @Override
    public User updateUser(User user) {

        return storage.updateUser(user);
    }

    @Override
    public User getUser(Long id) {


        Optional<User> userOptional = storage.getUser(id);
        if (!userOptional.isPresent()) {
            throw new EntityNotFoundException("Пользователь с ID " + id + " не найден");
        }
        return userOptional.get();
    }

    @Override
    public void addFriend(long userId, long friendId) {


        if (userId == friendId) {
            throw new ValidationException("Пользователь не может добавить себя в друзья");
        }

        getUser(userId);
        getUser(friendId);

        storage.addFriend(userId, friendId);
    }

    @Override
    public void removeFriend(long userId, long friendId) {

        getUser(userId);
        getUser(friendId);

        storage.removeFriend(userId, friendId);
    }

    @Override
    public List<User> getFriends(long userId) {

        getUser(userId);

        return storage.getFriends(userId);
    }

    @Override
    public List<User> getCommonFriends(long userId, long otherId) {

        getUser(userId);
        getUser(otherId);

        return storage.getCommonFriends(userId, otherId);
    }
}
