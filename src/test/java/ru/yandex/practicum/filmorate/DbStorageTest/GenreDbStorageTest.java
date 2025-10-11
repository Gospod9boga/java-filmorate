package ru.yandex.practicum.filmorate.DbStorageTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreDbStorage;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@ActiveProfiles("test")
@Import(GenreDbStorage.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class GenreDbStorageTest {

    private final GenreDbStorage genreStorage;
    private final JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {

        jdbcTemplate.execute("DELETE FROM films_genres");
        jdbcTemplate.execute("DELETE FROM genres");

        jdbcTemplate.execute("INSERT INTO genres (id, name) VALUES " +
                "(1, 'Комедия'), " +
                "(2, 'Драма'), " +
                "(3, 'Мультфильм'), " +
                "(4, 'Триллер'), " +
                "(5, 'Документальный'), " +
                "(6, 'Боевик')");
    }

    @Test
    @DisplayName("Should return all genres")
    void should_Return_All_Genres() {

        List<Genre> genres = genreStorage.findAll();

        assertThat(genres).hasSize(6);
        assertThat(genres)
                .extracting(Genre::getName)
                .containsExactlyInAnyOrder(
                        "Комедия", "Драма", "Мультфильм",
                        "Триллер", "Документальный", "Боевик"
                );
    }

    @Test
    @DisplayName("Should return genre when find by existing id")
    void should_Return_Genre_When_Find_By_Existing_Id() {

        Optional<Genre> genre = genreStorage.findById(1L);

        assertThat(genre)
                .isPresent()
                .get()
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("name", "Комедия");
    }

    @Test
    @DisplayName("Should return genre when find by different id")
    void should_Return_Genre_When_Find_By_Different_Id() {

        Optional<Genre> genre = genreStorage.findById(2L);

        assertThat(genre)
                .isPresent()
                .get()
                .hasFieldOrPropertyWithValue("id", 2L)
                .hasFieldOrPropertyWithValue("name", "Драма");
    }

    @Test
    @DisplayName("Should return empty when find genre by non-existing id")
    void should_Return_Empty_When_Find_Genre_By_Non_Existing_Id() {

        Optional<Genre> genre = genreStorage.findById(999L);

        assertThat(genre).isEmpty();
    }

    @Test
    @DisplayName("Should return empty when find genre by zero id")
    void should_Return_Empty_When_Find_Genre_By_Zero_Id() {

        Optional<Genre> genre = genreStorage.findById(0L);

        assertThat(genre).isEmpty();
    }

    @Test
    @DisplayName("Should return empty when find genre by negative id")
    void should_Return_Empty_When_Find_Genre_By_Negative_Id() {

        Optional<Genre> genre = genreStorage.findById(-1L);

        assertThat(genre).isEmpty();
    }
}
