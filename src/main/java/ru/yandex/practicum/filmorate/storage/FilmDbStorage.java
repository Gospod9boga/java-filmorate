package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.ValidationException.ValidationException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;


import java.sql.*;
import java.sql.Date;
import java.util.*;
import java.util.stream.Collectors;


@Repository
@Qualifier("filmDbStorage")
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final FilmMapper filmMapper = new FilmMapper();

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film create(Film film) {
        try {
            String sql = "INSERT INTO films (name, description, release_date, duration, mpa_rating_id) " +
                    "VALUES (?, ?, ?, ?, ?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();

            jdbcTemplate.update(connection -> {
                PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"id"});
                stmt.setString(1, film.getName());
                stmt.setString(2, film.getDescription());
                stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
                stmt.setInt(4, film.getDuration());
                stmt.setLong(5, film.getMpa().getId());
                return stmt;
            }, keyHolder);

            film.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
            saveGenres(film);
            return film;
        } catch (Exception e) {
            throw new ValidationException("Ошибка при создании фильма: " + e.getMessage());
        }
    }

    @Override
    public Optional<Film> get(long id) {
        try {
            String sql = "SELECT f.*, m.name as mpa_name FROM films f " +
                    "LEFT JOIN mpa_ratings m ON f.mpa_rating_id = m.id " +
                    "WHERE f.id = ?";
            List<Film> films = jdbcTemplate.query(sql, filmMapper, id);
            Film film = films.stream().findFirst().orElse(null);

            if (film != null) {
                loadGenres(film);
                loadLikes(film);
                return Optional.of(film);
            }
            return Optional.empty();
        } catch (Exception e) {
            throw new ValidationException("Ошибка при получении фильма с id " + id + ": " + e.getMessage());
        }
    }

    @Override
    public List<Film> getAll() {
        try {
            String sql = "SELECT f.*, m.name as mpa_name FROM films f " +
                    "LEFT JOIN mpa_ratings m ON f.mpa_rating_id = m.id";
            List<Film> films = jdbcTemplate.query(sql, filmMapper);

            for (Film film : films) {
                loadGenres(film);
                loadLikes(film);
            }

            return films;
        } catch (Exception e) {
            throw new ValidationException("Ошибка при получении всех фильмов: " + e.getMessage());
        }
    }

    @Override
    public Film updateFilm(Film film) {
        try {
            String sql = "UPDATE films SET name = ?, description = ?, release_date = ?, " +
                    "duration = ?, mpa_rating_id = ? WHERE id = ?";
            jdbcTemplate.update(sql,
                    film.getName(),
                    film.getDescription(),
                    film.getReleaseDate(),
                    film.getDuration(),
                    film.getMpa().getId(),
                    film.getId());

            String deleteGenresSql = "DELETE FROM films_genres WHERE film_id = ?";
            jdbcTemplate.update(deleteGenresSql, film.getId());
            saveGenres(film);

            return film;
        } catch (Exception e) {
            throw new ValidationException("Ошибка при обновлении фильма с id " + film.getId() + ": " + e.getMessage());
        }
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        try {
            String sql = "SELECT f.*, m.name as mpa_name, COUNT(l.user_id) as likes_count " +
                    "FROM films f " +
                    "LEFT JOIN mpa_ratings m ON f.mpa_rating_id = m.id " +
                    "LEFT JOIN likes l ON f.id = l.film_id " +
                    "GROUP BY f.id, m.name " +
                    "ORDER BY likes_count DESC " +
                    "LIMIT ?";

            List<Film> films = jdbcTemplate.query(sql, filmMapper, count);

            for (Film film : films) {
                loadGenres(film);
                loadLikes(film);
            }

            return films;
        } catch (Exception e) {
            throw new ValidationException("Ошибка при получении популярных фильмов: " + e.getMessage());
        }
    }

    @Override
    public void addLike(long filmId, long userId) {
        try {
            String sql = "INSERT INTO likes (film_id, user_id) VALUES (?, ?)";
            jdbcTemplate.update(sql, filmId, userId);
        } catch (Exception e) {
            throw new ValidationException("Ошибка при добавлении лайка фильму " + filmId + " от пользователя " + userId + ": " + e.getMessage());
        }
    }

    @Override
    public void removeLike(long filmId, long userId) {
        try {
            String sql = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
            jdbcTemplate.update(sql, filmId, userId);
        } catch (Exception e) {
            throw new ValidationException("Ошибка при удалении лайка фильму " + filmId + " от пользователя " + userId + ": " + e.getMessage());
        }
    }

    private void saveGenres(Film film) {
        try {
            if (film.getGenres() != null && !film.getGenres().isEmpty()) {
                Set<Long> genreIds = film.getGenres().stream()
                        .map(Genre::getId)
                        .collect(Collectors.toSet());

                for (Long genreId : genreIds) {
                    String sql = "INSERT INTO films_genres (film_id, genre_id) VALUES (?, ?)";
                    jdbcTemplate.update(sql, film.getId(), genreId);
                }
            }
        } catch (Exception e) {
            throw new ValidationException("Ошибка при сохранении жанров для фильма " + film.getId() + ": " + e.getMessage());
        }
    }

    private void loadGenres(Film film) {
        try {
            String sql = "SELECT g.id, g.name FROM genres g " +
                    "JOIN films_genres fg ON g.id = fg.genre_id " +
                    "WHERE fg.film_id = ?";
            List<Genre> genres = jdbcTemplate.query(sql, (rs, rowNum) -> {
                Genre genre = new Genre();
                genre.setId(rs.getLong("id"));
                genre.setName(rs.getString("name"));
                return genre;
            }, film.getId());
            film.setGenres(genres);
        } catch (Exception e) {
            throw new ValidationException("Ошибка при загрузке жанров для фильма " + film.getId() + ": " + e.getMessage());
        }
    }

    private void loadLikes(Film film) {
        try {
            String sql = "SELECT user_id FROM likes WHERE film_id = ?";
            List<Long> likes = jdbcTemplate.query(sql, (rs, rowNum) -> rs.getLong("user_id"), film.getId());
            film.getLikes().addAll(likes);
        } catch (Exception e) {
            throw new ValidationException("Ошибка при загрузке лайков для фильма " + film.getId() + ": " + e.getMessage());
        }
    }

}
