package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class UserService {
    private UserStorage userStorage;
    private FriendStorage friendStorage;

    public UserService(UserStorage userStorage, FriendStorage friendStorage) {
        this.userStorage = userStorage;
        this.friendStorage = friendStorage;
    }

    public User createUser(User user) {
        if (validateUser(user)) {
            userStorage.createUser(user);
        }
        return user;
    }

    public List<User> getUsers() {

        return userStorage.getUsers();
    }

    public void updateUser(User user) {

        userStorage.updateUser(user);
    }

    public User findId(Long id) {

        return userStorage.getUserById(id);
    }

    public void addFriend(Long userId, Long friendId) {
        if (userId == friendId) {
            throw new ValidationException("Нельзя добавить самого себя в друзья!");
        }
        friendStorage.addFriend(userId, friendId);
    }

    public void deleteFriend(Long userId, Long friendId) {
        if (userId == friendId) {
            throw new ValidationException("Нельзя удалить самого себя из друзей!");
        }
        friendStorage.deleteFriend(userId, friendId);
    }

    public List<User> getFriends(Long Id) {
        return friendStorage.getFriends(Id);
    }

    public List getCommonFriends(Long id1, Long id2) {
        return friendStorage.getCommonFriends(id1, id2);
    }

    public boolean validateUser(User user) {
        if (user.getLogin().contains(" ")) {
            log.info("Пользователь не добавлен: {}", user);
            throw new ValidationException("Логин не может быть пустым или содержать пробелы");
        }
        if (Objects.isNull(user.getName()) || user.getName().isBlank()) {
            log.info("Имя не указано, будет использован login пользователя.");

        }
        if (user.getBirthday().isAfter((LocalDate.now()))) {
            log.info("Пользователь не добавлен: {}", user);
            throw new ValidationException("Дата рождения не может быть в будущем времени");
        }
        return true;
    }
}