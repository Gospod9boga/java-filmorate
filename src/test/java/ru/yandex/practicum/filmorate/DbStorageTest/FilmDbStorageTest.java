package ru.yandex.practicum.filmorate.DbStorageTest;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.MpaDbStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@ActiveProfiles("test")
@Import({FilmDbStorage.class, MpaDbStorage.class, GenreDbStorage.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmDbStorageTest {


    private final FilmDbStorage filmStorage;
    private final JdbcTemplate jdbcTemplate;
    private Mpa testMpa;

    @BeforeEach
    void setUp() {

        jdbcTemplate.execute("DELETE FROM likes");
        jdbcTemplate.execute("DELETE FROM films_genres");
        jdbcTemplate.execute("DELETE FROM films");
        jdbcTemplate.execute("DELETE FROM users");
        jdbcTemplate.execute("DELETE FROM genres");
        jdbcTemplate.execute("DELETE FROM mpa_ratings");


        jdbcTemplate.execute("INSERT INTO mpa_ratings (id, name) VALUES (1, 'G'), (2, 'PG'), (3, 'PG-13')");
        jdbcTemplate.execute("INSERT INTO genres (id, name) VALUES (1, 'Комедия'), (2, 'Драма'), (3, 'Мультфильм')");

        testMpa = new Mpa();
        testMpa.setId(1L);
        testMpa.setName("G");
    }

    @Test
    @DisplayName("Should return film when find by existing id")
    void should_Return_Film_When_Find_By_Existing_Id() {

        Film film = createTestFilm("Test Film", "Test Description");
        Film savedFilm = filmStorage.create(film);

        Optional<Film> foundFilm = filmStorage.get(savedFilm.getId());

        assertThat(foundFilm)
                .isPresent()
                .get()
                .hasFieldOrPropertyWithValue("id", savedFilm.getId())
                .hasFieldOrPropertyWithValue("name", "Test Film")
                .hasFieldOrPropertyWithValue("description", "Test Description");
    }

    @Test
    @DisplayName("Should return empty when find by non-existing id")
    void should_Return_Empty_When_Find_By_Non_Existing_Id() {
        Optional<Film> foundFilm = filmStorage.get(999L);
        assertThat(foundFilm).isEmpty();
    }

    @Test
    @DisplayName("Should create film with generated id")
    void should_Create_Film_With_Generated_Id() {

        Film film = createTestFilm("New Film", "New Description");

        Film savedFilm = filmStorage.create(film);

        assertThat(savedFilm).isNotNull();
        assertThat(savedFilm.getId()).isNotNull();
        assertThat(savedFilm.getName()).isEqualTo("New Film");
        assertThat(savedFilm.getDescription()).isEqualTo("New Description");
        assertThat(savedFilm.getMpa()).isNotNull();
    }

    @Test
    @DisplayName("Should update film when film exists")
    void should_Update_Film_When_Film_Exists() {

        Film film = createTestFilm("Original Film", "Original Description");
        Film savedFilm = filmStorage.create(film);

        savedFilm.setName("Updated Film");
        savedFilm.setDescription("Updated Description");
        Film updatedFilm = filmStorage.updateFilm(savedFilm);

        assertThat(updatedFilm.getName()).isEqualTo("Updated Film");
        assertThat(updatedFilm.getDescription()).isEqualTo("Updated Description");
    }

    @Test
    @DisplayName("Should return all films")
    void should_Return_All_Films() {

        Film film1 = createTestFilm("Film One", "Description One");
        Film film2 = createTestFilm("Film Two", "Description Two");
        filmStorage.create(film1);
        filmStorage.create(film2);

        List<Film> films = filmStorage.getAll();

        assertThat(films).hasSize(2);
        assertThat(films)
                .extracting(Film::getName)
                .contains("Film One", "Film Two");
    }

    @Test
    @DisplayName("Should add like when film and user exist")
    void should_Add_Like_When_Film_And_User_Exist() {

        Film film = createTestFilm("Film with like", "Description");
        User user = createTestUser();
        Film savedFilm = filmStorage.create(film);
        Long savedUserId = createUserInDb(user);

        filmStorage.addLike(savedFilm.getId(), savedUserId);
        Optional<Film> filmWithLike = filmStorage.get(savedFilm.getId());

        assertThat(filmWithLike)
                .isPresent()
                .get()
                .extracting(Film::getLikes)
                .asInstanceOf(InstanceOfAssertFactories.COLLECTION) // Добавляем эту строку
                .contains(savedUserId);
    }

    @Test
    @DisplayName("Should remove like when like exists")
    void should_Remove_Like_When_Like_Exists() {

        Film film = createTestFilm("Film with like", "Description");
        User user = createTestUser();
        Film savedFilm = filmStorage.create(film);
        Long savedUserId = createUserInDb(user);
        filmStorage.addLike(savedFilm.getId(), savedUserId);

        filmStorage.removeLike(savedFilm.getId(), savedUserId);
        Optional<Film> filmWithoutLike = filmStorage.get(savedFilm.getId());

        assertThat(filmWithoutLike).isPresent();
        Film actualFilm = filmWithoutLike.get();
        assertThat(actualFilm.getLikes()).doesNotContain(savedUserId); // Простая проверка Set
    }

    @Test
    @DisplayName("Should return popular films ordered by likes count")
    void should_Return_Popular_Films_Ordered_By_Likes_Count() {

        Film film1 = createTestFilm("Popular Film", "Most liked");
        Film film2 = createTestFilm("Less Popular Film", "Less liked");
        User user1 = createTestUser("user1@mail.ru", "user1");
        User user2 = createTestUser("user2@mail.ru", "user2");

        Film savedFilm1 = filmStorage.create(film1);
        Film savedFilm2 = filmStorage.create(film2);
        Long savedUserId1 = createUserInDb(user1);
        Long savedUserId2 = createUserInDb(user2);

        filmStorage.addLike(savedFilm1.getId(), savedUserId1);
        filmStorage.addLike(savedFilm1.getId(), savedUserId2);
        filmStorage.addLike(savedFilm2.getId(), savedUserId1);

        List<Film> popularFilms = filmStorage.getPopularFilms(2);

        assertThat(popularFilms).hasSize(2);
        assertThat(popularFilms.get(0).getId()).isEqualTo(savedFilm1.getId());
        assertThat(popularFilms.get(0).getLikes()).hasSize(2);
        assertThat(popularFilms.get(1).getId()).isEqualTo(savedFilm2.getId());
        assertThat(popularFilms.get(1).getLikes()).hasSize(1);
    }

    private Film createTestFilm(String name, String description) {
        Film film = new Film();
        film.setName(name);
        film.setDescription(description);
        film.setReleaseDate(LocalDate.of(2020, 1, 1));
        film.setDuration(120);
        film.setMpa(testMpa);
        return film;
    }

    private User createTestUser() {
        return createTestUser("test@mail.ru", "testuser");
    }

    private User createTestUser(String email, String login) {
        User user = new User();
        user.setEmail(email);
        user.setLogin(login);
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        return user;
    }

    private Long createUserInDb(User user) {
        String sql = "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());

        String getIdSql = "SELECT id FROM users WHERE email = ?";
        return jdbcTemplate.queryForObject(getIdSql, Long.class, user.getEmail());
    }
}
