package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.ValidationException.EntityNotFoundException;
import ru.yandex.practicum.filmorate.ValidationException.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserStorage storage;

    public UserServiceImpl(UserStorage storage) {
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
        User user = storage.getUser(id);
        if (user == null) {
            throw new EntityNotFoundException("Пользователь с ID " + id + " не найден");
        }
        return user;
    }

    @Override
    public void addFriend(long userId, long friendId) {
        if (userId == friendId) {
            throw new ValidationException("Пользователь не может добавить себя в друзья");
        }
        User user = getUser(userId);
        User friend = getUser(friendId);

        user.getFriends().add(friendId);
        friend.getFriends().add(userId);

        storage.updateUser(user);
        storage.updateUser(friend);
    }

    @Override
    public void removeFriend(long userId, long friendId) {
        User user = getUser(userId);
        User friend = getUser(friendId);

        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);

        storage.updateUser(user);
        storage.updateUser(friend);
    }

    @Override
    public List<User> getFriends(long userId) {
        User user = getUser(userId);
        Set<Long> friendIds = user.getFriends();

        return friendIds.stream()
                .map(storage::getUser)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> getCommonFriends(long userId, long otherId) {
        User user = getUser(userId);
        User other = getUser(otherId);

        Set<Long> userFriends = user.getFriends();
        Set<Long> otherFriends = other.getFriends();

        return userFriends.stream()
                .filter(otherFriends::contains)
                .map(storage::getUser)
                .collect(Collectors.toList());
    }
}
