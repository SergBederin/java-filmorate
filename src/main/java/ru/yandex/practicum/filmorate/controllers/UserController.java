package ru.yandex.practicum.filmorate.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.*;

@RestController
public class UserController {
    private final Map<Long, User> users = new HashMap<>();
    private long idUser = 0;
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @RequestMapping("/home")
    public String homePage() {
        return "FILMORATE";
    }

   @PostMapping(value = "/users")
   public User addUser(@Valid @RequestBody User user) {
       log.info("Отправлен запрос Post:/users");
       validateUser(user);
       user.setId(++idUser);
       users.put(user.getId(), user);
       log.debug("Добавлен пользователь: {}", user);
       return user;
   }

    @PutMapping(value = "/users")
    public User updateUser(@Valid @RequestBody User user) {
        log.info("Отправлен запрос Put:/users");
        User oldUser;
        if (users.containsKey(user.getId())) {
            oldUser = users.get(user.getId());
            users.put(user.getId(), user);
            log.debug("Пользователь {} изменен на {}", oldUser, user);
        } else {
            throw new ValidationException("Нет пользователя с таким ID");
        }
        return user;
    }

    @GetMapping("/users")
    public List<User> getUsers() {
        log.info("Отправлен запрос GET:/users");
        List<User> userList = new ArrayList<>(users.values());
        log.debug("Всего пользователей: {}", users.size());
        return userList;
    }

    private void validateUser(User user) {
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
