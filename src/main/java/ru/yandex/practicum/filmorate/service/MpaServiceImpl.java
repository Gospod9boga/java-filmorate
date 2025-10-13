package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.ValidationException.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.List;

@Service
public class MpaServiceImpl implements MpaService {

    private final MpaStorage mpaStorage;

    public MpaServiceImpl(MpaStorage mpaStorage) {
        this.mpaStorage = mpaStorage;
    }

    @Override
    public List<Mpa> findAll() {
        return mpaStorage.findAll();
    }

    @Override
    public Mpa findById(Long id) {

        return mpaStorage.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("MPA рейтинг с ID " + id + " не найден"));
    }
}
