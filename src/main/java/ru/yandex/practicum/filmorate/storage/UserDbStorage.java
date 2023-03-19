package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Repository
@Slf4j
@Primary
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List getUsers() {
        String sql = "SELECT user_id, login, name, email, birthday " +
                    "FROM Users;";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs));
    }

    @Override
    public User createUser(User user) {
        user.setName(returnUserName(user));
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("Users")
                .usingGeneratedKeyColumns("user_id");
        user.setId(simpleJdbcInsert.executeAndReturnKey(user.toMap()).longValue());
        log.info("Добавлен новый пользователь с ID={}", user.getId());
        return user;
    }

    @Override
    public User updateUser(User user) {
        checkUserContains(user.getId());
        String sql = "UPDATE Users " +
                "SET login = ?," +
                "    name = ?," +
                "    email = ?," +
                "    birthday = ?" +
                "WHERE user_id = ?;";
        jdbcTemplate.update(sql, user.getLogin(), user.getName(), user.getEmail(), user.getBirthday(), user.getId());
        return user;
    }

    @Override
    public User getUserById(Long id) {
        checkUserContains(id);
        String sql = "SELECT user_id, login, name, email, birthday " +
                "FROM Users " +
                "WHERE user_id = ?;";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> makeUser(rs), id);
    }

    @Override
    public void deleteUser(Long id) {
        checkUserContains(id);
        String sql = "DELETE FROM Users WHERE user_id = ?;";
        jdbcTemplate.update(sql, id);
    }

    private String returnUserName(User user) {
        if (Objects.isNull(user.getName()) || user.getName().isBlank() || user.getName().isEmpty()) {
            return user.getLogin();
        } else {
            return user.getName();
        }
    }

    @Override
    public void checkUserContains(Long id) {
        String sql = "SELECT COUNT(1) AS row_count FROM Users WHERE user_id = ?;";
        Long rowCount = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> rs.getLong("row_count"), id);
        if (rowCount == 0) {
            throw new NotFoundException("Пользователь с ID=" + id + " не найден!");
        }
    }

    private User makeUser(ResultSet rs) throws SQLException {

        Long id = rs.getLong("user_id");
        String login = rs.getString("login");
        String name = rs.getString("name");
        String email = rs.getString("email");
        LocalDate birthday = rs.getDate("birthday").toLocalDate();
        return new User(id, email, login, name, birthday);
    }
}