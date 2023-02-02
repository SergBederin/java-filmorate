package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controllers.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserControllerTest {
      static UserController userController = new UserController();

    @Test
    void shouldValidateEmptyLogin() {
        User user = new User();
        user.setEmail("user@mail.ru");
        user.setBirthday(LocalDate.of(2023, 01, 01));
        user.setLogin("Login login");
        Exception exception = assertThrows(ValidationException.class, () -> userController.addUser(user));
        assertEquals("Логин не может быть пустым или содержать пробелы", exception.getMessage());
    }

    @Test
    void shouldValidateUserEmptyName() {
        User user = new User();
        user.setEmail("user@mail.ru");
        user.setLogin("Login");
        user.setBirthday(LocalDate.of(2012, 12, 12));
        userController.addUser(user);
        assertEquals("Login", user.getName());
    }

    @Test
    void shouldValidationBirthdayTest() {
        User user = new User();
        user.setName("Name");
        user.setLogin("Login");
        user.setEmail("name@mail.ru");
        user.setBirthday(LocalDate.of(2024, 01, 01));
        Exception exception = assertThrows(ValidationException.class, () -> userController.addUser(user));
        assertEquals("Дата рождения не может быть в будущем времени", exception.getMessage());

    }
}
