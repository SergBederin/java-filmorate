package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private long idUser = 1L;
    private final Map<Long, User> storage = new HashMap<>();

    @Override
    public User createUser(User user) {
        user.setName(returnUserName(user));
        user.setId(idUser);
        storage.put(idUser++, user);
        log.debug("Добавлен пользователь: {}", user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (storage.containsKey(user.getId())) {
            storage.put(user.getId(), user);
        } else {
            throw new NotFoundException("Нет такого ID: " + user.getId());
        }
        return user;
    }

    @Override
    public List<User> getUsers() {
        List<User> userList = new ArrayList<>(storage.values());
        log.debug("Текущее количество пользователей: {}", storage.size());
        return userList;
    }

    @Override
    public User getUserById(Long userId) {
        if (!storage.containsKey(userId)) {
            throw new NotFoundException("Пользователь с ID=" + userId + " не найден!");
        }
        return storage.get(userId);
    }

    @Override
    public void deleteUser(Long userId) {
        if (userId == null) {
            throw new ValidationException("Передан пустой аргумент!");
        }
        if (!storage.containsKey(userId)) {
            throw new NotFoundException("Пользователь с ID=" + userId + " не найден!");
        }
        // удаляем из списка друзей пользователя у других пользователей
        for (User user : storage.values()) {
            user.getFriends().remove(userId);
        }
        storage.remove(userId);
    }

    @Override
    public void checkUserContains(Long id) {

    }

    @Override
    public void addFriend(Long userId, Long friendId) {

    }

    @Override
    public void deleteFriend(Long userId, Long friendId) {

    }

    @Override
    public List<User> getFriends(Long Id) {
        return null;
    }

    @Override
    public List getCommonFriends(Long id1, Long id2) {
        return null;
    }


    private String returnUserName(User user) {
        if (Objects.isNull(user.getName()) || user.getName().isBlank() || user.getName().isEmpty()) {
            return user.getLogin();
        } else {
            return user.getName();
        }
    }
}
