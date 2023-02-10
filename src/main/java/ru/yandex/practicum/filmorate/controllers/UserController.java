package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController extends AbstractController<User> {
    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        log.info("Отправлен запрос Post:/users");
        validate(user);
        super.create(user);
        log.info("Добавлен пользователь: {}", user);
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        log.info("Отправлен запрос Put:/users");
        User newUser = super.update(user);
        log.info("Пользователь {} изменен", newUser);
        return newUser;
    }

    @GetMapping
    public List<User> getUsers() {
        log.info("Отправлен запрос GET:/users");
        List<User> userList = super.allData();
        log.info("Всего пользователей: {}", userList.size());
        return userList;
    }

    void validate(User user) {
        if (user.getLogin().contains(" ")) {
            throw new ValidationException("Логин не может быть пустым или содержать пробелы");
        }
        if (Objects.isNull(user.getName()) || user.getName().isBlank()) {
            log.info("Имя не указано, будет использован login пользователя.");
            user.setName(user.getLogin());

        }
        if (user.getBirthday().isAfter((LocalDate.now()))) {
            throw new ValidationException("Дата рождения не может быть в будущем времени");
        }
    }

}
