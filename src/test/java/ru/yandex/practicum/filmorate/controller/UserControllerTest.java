package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controllers.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class UserControllerTest {
    private User user;
    private UserController userController;
    private UserStorage userStorage;

    @BeforeEach
    public void beforeEach() {
        user = new User();
        userStorage = new InMemoryUserStorage();
        userController = new UserController(new UserService(userStorage));
        user.setLogin("login");
        user.setEmail("name@ya.ru");
        user.setName("name");
        user.setBirthday(LocalDate.of(1986, 01, 01));
    }

    @Test
    void shouldValidateEmptyLogin() {
        //User user = new User();
        user.setLogin("Login login");
        Exception exception = assertThrows(ValidationException.class, () -> userController.createUser(user));
        assertEquals("Логин не может быть пустым или содержать пробелы", exception.getMessage());
    }

    @Test
    void shouldValidateUserEmptyName() {
        // User user = new User();
        user.setName(" ");
        user.setEmail("user@mail.ru");
        user.setLogin("Login");
        user.setBirthday(LocalDate.of(2012, 12, 12));
        userController.createUser(user);
        assertEquals("Login", user.getName());
    }

    @Test
    void shouldValidationBirthdayTest() {
        //  User user = new User();
        user.setName("Name");
        user.setLogin("Login");
        user.setEmail("name@mail.ru");
        user.setBirthday(LocalDate.of(2024, 01, 01));
        Exception exception = assertThrows(ValidationException.class, () -> userController.createUser(user));
        assertEquals("Дата рождения не может быть в будущем времени", exception.getMessage());
    }
}
