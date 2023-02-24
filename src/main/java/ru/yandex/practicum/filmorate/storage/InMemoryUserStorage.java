package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.*;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private long idUser = 1L;
    private final Map<Long, User> storage = new HashMap<>();

    @Override
    public User createUser(User user) {
        if (UserService.validateUser(user)) {
            user.setName(returnUserName(user));
            user.setId(idUser);
            storage.put(idUser++, user);
            log.debug("Добавлен пользователь: {}", user);
        }
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
    public void deleteUser(User user) {
        if (!storage.containsKey(user.getId())) {
            throw new NotFoundException("Нет такого ID: " + user.getId());
        }
        storage.remove(user.getId());
        log.debug("Пользователь {} удалён", user);
    }

    @Override
    public User findId(Long id) {
        if (storage.containsKey(id)) {
            return storage.get(id);
        } else {
            throw new NotFoundException("Нет такого ID: " + id);
        }
    }

    @Override
    public List<User> allData() {
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

    private String returnUserName(User user) {
        if (Objects.isNull(user.getName()) || user.getName().isBlank() || user.getName().isEmpty()) {
            return user.getLogin();
        } else {
            return user.getName();
        }
    }
}
