package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository

public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Genre> getGenres() {
        String sql = "SELECT * FROM Genres";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeGenre(rs));
    }

    @Override
    public Genre getGenreById(Integer genreId) {
        String sql = "SELECT COUNT(1) AS row_count FROM Genres WHERE genre_id = ?;";
        int rowCount = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> rs.getInt("row_count"), genreId);
        if (rowCount == 0) {
            throw new NotFoundException("Жанр с ID=" + genreId + " не найден!");
        }
        sql = "SELECT genre_id, name FROM Genres WHERE genre_id = ?;";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> makeGenre(rs), genreId);
    }

    private Genre makeGenre(ResultSet rs) throws SQLException {
        Integer id = rs.getInt("genre_id");
        String name = rs.getString("name");
        return new Genre(id, name);
    }

}