package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
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

    public Film() {
    }
}
