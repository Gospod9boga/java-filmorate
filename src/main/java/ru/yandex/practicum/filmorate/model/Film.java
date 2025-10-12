package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Film {
    private Long id;

    @NotBlank(message = "Name must not be blank")
    private String name;

    @Size(max = 200, message = "Description must be at most 200 characters")
    private String description;

    @NotNull(message = "Release date must not be null")
    @PastOrPresent(message = "Release date must be in the past or present")
    private LocalDate releaseDate;

    @Positive(message = "Duration must be positive")
    private int duration;

    private Set<Long> likes = new HashSet<>();
    private List<Genre> genres = new ArrayList<>();
    private Mpa mpa;
}