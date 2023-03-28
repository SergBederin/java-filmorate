package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Repository
@Slf4j
@Primary
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film create(Film film) {
        List<Genre> genresList = film.getGenres();

        String sqlQuery = "INSERT INTO Films (name, description, releaseDate, duration, rating_id) " +
                "VALUES (?, ?, ?, ?, ?);";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sqlQuery, new String[]{"film_id"});
            statement.setString(1, film.getName());
            statement.setString(2, film.getDescription());
            statement.setDate(3, Date.valueOf(film.getReleaseDate()));
            statement.setLong(4, film.getDuration());
            statement.setLong(5, film.getMpa().getId());
            return statement;
        }, keyHolder);

        film.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        updateGenres(genresList, film.getId());
        film.setGenres(getFilmGenres(film.getId()));

        return film;
    }

    @Override
    public Film updateFilm(Film film) {

        checkFilm(film.getId());
        String sql = "UPDATE Films " +
                "   SET name = ?," +
                "    description = ?," +
                "    releaseDate = ?," +
                "    duration = ?," +
                "    rating_id = ? " +
                "WHERE film_id = ?;";
        jdbcTemplate.update(sql, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getMpa().getId(), film.getId());
        updateGenres(film.getGenres(), film.getId());
        film = getFilmById(film.getId());
        return film;
    }

    @Override
    public void deleteFilm(Film film) {
        String sqlQuery = "DELETE FROM Films WHERE film_id = ?";
        jdbcTemplate.update(sqlQuery, film.getId());
        log.info("Удалён фильм с идентификатором {}", film.getId());
    }

    @Override
    public Film getFilmById(Long id) {
        String sql = "SELECT f.film_id, f.name, f.description, f.releaseDate, f.duration, r.rating_id, r.name as mpa_name " +
                "FROM Films f " +
                "INNER JOIN Rating r ON r.rating_id = f.rating_id " +
                "WHERE f.film_id = ?;";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs), id)
                .stream()
                .findAny()
                .orElseThrow(() -> new NotFoundException("Фильм с id=" + id + " не существует"));
    }

    @Override
    public List<Film> getFilms() {
        String sql = "SELECT f.film_id, f.name, f.description, f.releaseDate, f.duration, r.rating_id, r.name as mpa_name " +
                "FROM Films f " +
                "INNER JOIN Rating r ON r.rating_id = f.rating_id " +
                "ORDER BY f.film_id ;";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs));
    }

    public List<Genre> getFilmGenres(Long film_id) {
        String sql = "SELECT g.genre_id, g.name from Genres_relation gr " +
                "INNER JOIN Genres g ON g.genre_id = gr.genre_id " +
                "WHERE film_id = ? " +
                "ORDER BY g.genre_id ;";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeGenre(rs), film_id);
    }

    private Film makeFilm(ResultSet rs) throws SQLException {
        Long id = rs.getLong("film_id");
        String name = rs.getString("name");
        String description = rs.getString("description");
        LocalDate releaseDate = rs.getDate("releaseDate").toLocalDate();
        Integer duration = rs.getInt("duration");
        Mpa mpa = new Mpa(rs.getInt("rating_id"), rs.getString("mpa_name"));

        String sql = "SELECT g.genre_id, g.name " +
                "FROM Genres_Relation gr " +
                "INNER JOIN Genres g ON g.genre_id = gr.genre_id " +
                "WHERE gr.film_id = ? " +
                "ORDER BY g.genre_id ASC; ";
        List<Genre> genres = jdbcTemplate.query(sql, (rx, rowNum) -> makeGenre(rx), id);
        sql = "SELECT DISTINCT user_id " +
                "FROM Likes " +
                "WHERE film_id = ? ;";

        List<Long> likes = jdbcTemplate.query(sql, (rz, rowNum) -> rz.getLong("user_id"), id);

        return new Film(id, name, description, releaseDate, duration, likes, mpa, genres);
    }

    private Genre makeGenre(ResultSet rs) throws SQLException {
        Integer id = rs.getInt("genre_id");
        String name = rs.getString("name");

        return new Genre(id, name);
    }

    private void updateGenres(List<Genre> genres, Long filmId) {
        deleteGenres(filmId);
        if (genres != null) {
            jdbcTemplate.batchUpdate(
                    "INSERT INTO Genres_relation(film_id, genre_id) " +
                            "SELECT ?, ? " +
                            "WHERE NOT EXISTS (SELECT 1 FROM Genres_relation " +
                            "WHERE film_id = ? AND genre_id = ?)",
                    new BatchPreparedStatementSetter() {
                        public void setValues(PreparedStatement ps, int i) throws SQLException {
                            ps.setLong(1, filmId);
                            ps.setInt(2, genres.get(i).getId());
                            ps.setLong(3, filmId);
                            ps.setInt(4, genres.get(i).getId());
                        }

                        public int getBatchSize() {
                            return genres.size();
                        }
                    });
        }
    }

    private void deleteGenres(Long filmId) {
        String sql = "DELETE FROM Genres_relation " +
                "WHERE film_id = ?;";
        jdbcTemplate.update(sql, filmId);
    }

    public void checkFilm(Long id) {
        String sql = "SELECT COUNT(1) AS  row_count " +
                "FROM Films " +
                "WHERE film_id = ?;";
        Long rowCount = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> rs.getLong("row_count"), id);
        if (rowCount == 0) {
            throw new NotFoundException(
                    String.format("Фильм c id= %d не найден!", id));
        }
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        deleteLike(filmId, userId);
        String sql = "INSERT INTO Likes(film_id, user_id) " +
                "VALUES(?,?); ";
        jdbcTemplate.update(sql, filmId, userId);
    }

    @Override
    public void deleteLike(Long id, Long userId) {
        if (userId < 0) {
            throw new NotFoundException(" ID не может быть отрицательным!");
        }
        String sqlQuery = "DELETE FROM Likes " +
                "WHERE film_id = ? AND user_id = ?;";
        jdbcTemplate.update(sqlQuery, id, userId);
    }
}

