package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserStorage userStorage;

    public User createUser(User user) {
        if (validateUser(user)) {
            userStorage.createUser(user);
        }
        return user;
    }

    public User updateUser(User user) {

        return userStorage.updateUser(user);
    }

    public List<User> getUsers() {

        return userStorage.allData();
    }

    public User findId(Long id) {

        return userStorage.findId(id);
    }

    public void addFriend(Long userId, Long friendId) throws NotFoundException {
        final User user = userStorage.findId(userId);
        final User friend = userStorage.findId(friendId);
        log.info("Пользователь : {}", user);
        log.info("Друг: {}", friend);
        if ((user != null) && (friend != null)) {
            user.getFriends().add(friendId);
            friend.getFriends().add(userId);
            userStorage.updateUser(user);
            log.info("Друзья пользователя: {}", user);
            userStorage.updateUser(friend);
            log.info("Друзья друга: {}", friend);
        }
    }

    public void deleteFriend(Long userId, Long friendId) {
        User user = userStorage.findId(userId);
        User friend = userStorage.findId(friendId);
        Set<Long> userFriends = user.getFriends();
        userFriends.remove(friendId);
        user.setFriends(userFriends);
        userStorage.updateUser(user);
        Set<Long> friendFriends = friend.getFriends();
        friendFriends.remove(userId);
        friend.setFriends(friendFriends);
        userStorage.updateUser(friend);
    }

    public List<User> getFriends(Long userId) throws NotFoundException {
        userStorage.getUserById(userId);
        List<User> friends = new ArrayList<>();
        Set<Long> friendsIds = userStorage.findId(userId).getFriends();
        if (friendsIds == null) {
            return friends;
        }
        for (Long friendId : friendsIds) {
            User friend = userStorage.findId(friendId);
            friends.add(friend);
        }
        return friends;
    }

    public List<User> getCommonFriends(Long id, Long otherId) {
        List<User> first = getFriends(id);
        List<User> second = getFriends(otherId);
        return first.stream().filter(second::contains).collect(Collectors.toList());
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
