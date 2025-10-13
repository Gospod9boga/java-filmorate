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
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaDbStorage;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@ActiveProfiles("test")
@Import(MpaDbStorage.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class MpaDbStorageTest {

    private final MpaDbStorage mpaStorage;
    private final JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {

        jdbcTemplate.execute("DELETE FROM films");
        jdbcTemplate.execute("DELETE FROM mpa_ratings");
        jdbcTemplate.execute("INSERT INTO mpa_ratings (id, name) VALUES " +
                "(1, 'G'), " +
                "(2, 'PG'), " +
                "(3, 'PG-13'), " +
                "(4, 'R'), " +
                "(5, 'NC-17')");
    }

    @Test
    @DisplayName("Should return all MPA ratings")
    void should_Return_All_MPA_Ratings() {

        List<Mpa> mpaList = mpaStorage.findAll();
        assertThat(mpaList).hasSize(5);
        assertThat(mpaList)
                .extracting(Mpa::getName)
                .containsExactlyInAnyOrder("G", "PG", "PG-13", "R", "NC-17");
    }

    @Test
    @DisplayName("Should return MPA rating when find by existing id")
    void should_Return_MPA_Rating_When_Find_By_Existing_Id() {

        Optional<Mpa> mpa = mpaStorage.findById(1L);
        assertThat(mpa)
                .isPresent()
                .get()
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("name", "G");
    }

    @Test
    @DisplayName("Should return empty when find MPA by non-existing id")
    void should_Return_Empty_When_Find_MPA_By_Non_Existing_Id() {
        Optional<Mpa> mpa = mpaStorage.findById(999L);
        assertThat(mpa).isEmpty();
    }
}
