package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
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
    public User getUser(Long id) {
        return users.get(id);
    }


    private long getNextId() {
        return nextId++;
    }
}
