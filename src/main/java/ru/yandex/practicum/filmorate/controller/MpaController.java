package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.constraints.Positive;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/mpa")
public class MpaController {

    private final MpaService mpaService;

    public MpaController(MpaService mpaService) {
        this.mpaService = mpaService;
    }

    @GetMapping
    public List<Mpa> findAll() {
        log.info("Получение всех MPA рейтингов");
        return mpaService.findAll();
    }

    @GetMapping("/{id}")
    public Mpa findById(@PathVariable @Positive Long id) {
        log.info("Получение MPA рейтинга с ID: {}", id);
        return mpaService.findById(id);
    }
}
