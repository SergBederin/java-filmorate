package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.*;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private long idUser = 1L;
    private final Map<Long, User> storage = new HashMap<>();

    @Override
    public User createUser(User user) {
        if (validateUser(user)) {
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
        // validateUser(user);
        if (storage.containsKey(user.getId())) {
            storage.remove(user.getId());
            log.debug("Пользователь {} удалён", user);
        } else {
            throw new NotFoundException("Нет такого ID: " + user.getId());
        }
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

    private boolean validateUser(User user) {
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

    private String returnUserName(User user) {
        if (Objects.isNull(user.getName()) || user.getName().isBlank() || user.getName().isEmpty()) {
            return user.getLogin();
        } else {
            return user.getName();
        }
    }

}
