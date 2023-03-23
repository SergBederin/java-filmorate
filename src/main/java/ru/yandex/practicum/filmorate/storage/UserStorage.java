package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    List<User> getUsers();

    User createUser(User user);

    User updateUser(User user);

    User getUserById(Long userId);

    void deleteUser(Long userId);

    public void checkUserContains(Long id);

    void addFriend(Long userId, Long friendId);

    void deleteFriend(Long userId, Long friendId);

    List<User> getFriends(Long Id);

    List getCommonFriends(Long id1, Long id2);

}