package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    User createUser(User user);

    User updateUser(User user);

    void deleteUser(User user);

    User findId(Long id);

    List<User> allData();

    User getUserById(Long ids);

}
