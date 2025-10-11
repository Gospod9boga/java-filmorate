package ru.yandex.practicum.filmorate.service;


import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.ValidationException.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.List;

@Service
public class GenreServiceImpl implements GenreService {

    private final GenreStorage genreStorage;

    public GenreServiceImpl(GenreStorage genreStorage) {
        this.genreStorage = genreStorage;
    }

    @Override
    public List<Genre> findAll() {
        return genreStorage.findAll();
    }

    @Override
    public Genre findById(Long id) {
        if (id == null || id <= 0) {
            throw new EntityNotFoundException("ID жанра должен быть положительным числом");
        }

        return genreStorage.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Жанр с ID " + id + " не найден"));
    }
}
