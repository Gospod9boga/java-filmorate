package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class User {

    private Long id;

    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Email must be a well-formed email address")
    private String email;

    @NotBlank(message = "Login cannot be empty")
    @Pattern(regexp = "\\S+", message = "Login cannot contain spaces")
    private String login;

    private String name;

    @NotNull(message = "Birthday cannot be null")
    @Past(message = "Birthday must be in the past")
    private LocalDate birthday;
}
