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
        String sql = "SELECT user_id, login, name, email, birthday " +
                "FROM Users " +
                "WHERE user_id = ?;";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs), id)
                .stream()
                .findAny()
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + id + " не существует"));
    }

    @Override
    public void deleteUser(Long id) {
        String sql = "DELETE FROM Users WHERE user_id = ?;";
        jdbcTemplate.update(sql, id);
        log.info("Удалён пользователь  {}", id);
    }

    private String returnUserName(User user) {
        if (Objects.isNull(user.getName()) || user.getName().isBlank() || user.getName().isEmpty()) {
            return user.getLogin();
        } else {
            return user.getName();
        }
    }

    public void addFriend(Long userId, Long friendId) {
        checkUserContains(userId);
        checkUserContains(friendId);
        String sql = "INSERT INTO Friendship(user_id, friend_id) " +
                "VALUES(?,?);";
        jdbcTemplate.update(sql, userId, friendId);
    }

    public void deleteFriend(Long friendId, Long userId) {
        String sql = "DELETE FROM Friendship WHERE user_id in (?,?) AND friend_id in (?,?);";
        jdbcTemplate.update(sql, userId, friendId, userId, friendId);
    }

    public List getFriends(Long Id) {
        String sql = "SELECT u.user_id, u.login, u.name, u.email, u.birthday " +
                "FROM Friendship f " +
                "INNER JOIN Users u ON u.user_id = f.friend_id " +
                "WHERE f.user_id = ? " +
                "ORDER BY u.user_id;";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs), Id);
    }

    public List getCommonFriends(Long id1, Long id2) {
        String sql = "SELECT u.user_id, u.login, u.name, u.email, u.birthday " +
                "FROM Friendship f1 " +
                "INNER JOIN Friendship f2 on f2.friend_id = f1.friend_id " +
                "INNER JOIN Users u on u.user_id = f2.friend_id " +
                "WHERE f1.user_id = ? and f2.user_id = ? " +
                "AND f1.friend_id <> f2.user_id AND f2.friend_id <> f1.user_id;";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs), id1, id2);
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