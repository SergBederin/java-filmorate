package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class MpaDbStorage implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Mpa> getAllMpa() {
        String sql = "SELECT rating_id, name FROM Rating;";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeMpa(rs));
    }

    @Override
    public Mpa getMpaById(Integer mpaId) {
        String sql = "SELECT COUNT(1) AS row_count FROM Rating WHERE rating_id = ?;";
        int rowCount = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> rs.getInt("row_count"), mpaId);
        if (rowCount == 0) {
            throw new NotFoundException("Рейтинг с ID=" + mpaId + " не найден!");
        }
        sql = "SELECT rating_id, name FROM Rating WHERE rating_id = ?;";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> makeMpa(rs), mpaId);
    }

    private Mpa makeMpa(ResultSet rs) throws SQLException {
        Integer id = rs.getInt("rating_id");
        String name = rs.getString("name");
        return new Mpa(id, name);
    }
}
