package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        return userService.createUser(user);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        log.info("Отправлен запрос Put:/users");
        userService.updateUser(user);
        return user;
    }

    @PutMapping(value = "/{id}/friends/{friendId}")
    public void addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("Отправлен запрос Put/users/{id}/friends/{friendId}");
        userService.addFriend(id, friendId);
        log.info("Пользователь {} добавлен в друзья", friendId);
    }

    @GetMapping
    public List<User> getUsers() {
        log.info("Отправлен запрос GET:/users");
        List<User> userList = userService.getUsers();
        log.info("Всего пользователей: {}", userList.size());
        return userList;
    }

    @GetMapping("/{id}")
    public Optional<User> find(@PathVariable Long id) {
        return Optional.ofNullable(userService.findId(id));
    }

    @GetMapping("/{id}/friends")
    public List<User> findFriends(@PathVariable Long id) {
        return userService.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable Long id, @PathVariable Long otherId) {
        log.info("Отправлен запрос GET:/{id}/friends/common/{otherId}");
        return userService.getCommonFriends(id, otherId);
    }

    @DeleteMapping(value = "/{id}/friends/{friendId}")
    public void delete(@PathVariable Long id, @PathVariable Long friendId) {
        userService.deleteFriend(id, friendId);
    }

}