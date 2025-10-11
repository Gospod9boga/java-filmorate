package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Qualifier("inMemoryUserStorage")
public class InMemoryUserStorage implements UserStorage {
    private Map<Long, User> users = new HashMap<>();
    private long nextId = 1;
    private Set<String> emails = new HashSet<>();

    @Override
    public User createUser(User user) {
        if (emails.contains(user.getEmail())) {
            throw new IllegalArgumentException("Email уже занят: " + user.getEmail());
        }
        user.setId(getNextId());
        users.put(user.getId(), user);
        emails.add(user.getEmail());
        return user;
    }

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User updateUser(User user) {
        User existing = users.get(user.getId());
        if (existing == null) {
            throw new IllegalArgumentException("Пользователь с ID " + user.getId() + " не найден");
        }

        if (!existing.getEmail().equals(user.getEmail()) && emails.contains(user.getEmail())) {
            throw new IllegalArgumentException("Email уже занят: " + user.getEmail());
        }

        emails.remove(existing.getEmail());
        emails.add(user.getEmail());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> getUser(Long id) {
        User user = users.get(id);
        return Optional.ofNullable(user);
    }

    @Override
    public void addFriend(long userId, long friendId) {
        User user = users.get(userId);
        User friend = users.get(friendId);

        if (user != null && friend != null) {
            user.getFriends().add(friendId);
            friend.getFriends().add(userId); // Двусторонняя дружба
        }
    }

    @Override
    public void removeFriend(long userId, long friendId) {
        User user = users.get(userId);
        User friend = users.get(friendId);

        if (user != null && friend != null) {
            user.getFriends().remove(friendId);
            friend.getFriends().remove(userId); // Удаляем с обеих сторон
        }
    }

    @Override
    public List<User> getFriends(long userId) {
        User user = users.get(userId);
        if (user == null) {
            return new ArrayList<>();
        }

        return user.getFriends().stream()
                .map(users::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> getCommonFriends(long userId, long otherId) {
        User user1 = users.get(userId);
        User user2 = users.get(otherId);

        if (user1 == null || user2 == null) {
            return new ArrayList<>();
        }

        Set<Long> commonFriendIds = new HashSet<>(user1.getFriends());
        commonFriendIds.retainAll(user2.getFriends());

        return commonFriendIds.stream()
                .map(users::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private long getNextId() {
        return nextId++;
    }
}
