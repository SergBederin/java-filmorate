package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.core.AutoConfigureCache;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@AutoConfigureCache
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmoRateApplicationTests {
    private final UserDbStorage userStorage;
    private final FilmDbStorage filmStorage;
    private final FilmService filmService;
    private final UserService userService;
    private User firstUser;
    private User secondUser;
    private User thirdUser;
    private Film firstFilm;
    private Film secondFilm;
    private Film thirdFilm;

    @BeforeEach
    public void beforeEach() {
        firstUser = User.builder()
                .name("Name1")
                .login("Login1")
                .email("user1@mail.ru")
                .birthday(LocalDate.of(2000, 01, 01))
                .build();

        secondUser = User.builder()
                .name("Name2")
                .login("Login2")
                .email("user2@mail.ru")
                .birthday(LocalDate.of(2001, 01, 01))
                .build();

        thirdUser = User.builder()
                .name("Name3")
                .login("Login3")
                .email("user3@mail.ru")
                .birthday(LocalDate.of(2002, 01, 01))
                .build();

        firstFilm = Film.builder()
                .name("Film1")
                .description("Смешная комедия.")
                .releaseDate(LocalDate.of(2023, 01, 1))
                .duration(100)
                .build();
        firstFilm.setMpa(new Mpa(1, "G"));
        firstFilm.setLikes(new ArrayList<>());
        //firstFilm.setGenres((List<Genre>) new Genre(1, "Комедия"));
        firstFilm.setGenres(new ArrayList<>(Arrays.asList(new Genre(2, "Драма")))
        );

        secondFilm = Film.builder()
                .name("Film2")
                .description("Лучший боевик")
                .releaseDate(LocalDate.of(2022, 12, 30))
                .duration(90)
                .build();
        secondFilm.setMpa(new Mpa(3, "PG-13"));
        secondFilm.setLikes(new ArrayList<>());
        //secondFilm.setGenres(new ArrayList<>(Arrays.asList(new Genre(6, "Боевик"))));
        secondFilm.setGenres(new ArrayList<>(Arrays.asList(new Genre(2, "Боевик")))
        );


        thirdFilm = Film.builder()
                .name("Film3")
                .description("Знаменитая драма")
                .releaseDate(LocalDate.of(1965, 01, 15))
                .duration(133)
                .build();
        thirdFilm.setMpa(new Mpa(4, "R"));
        thirdFilm.setLikes(new ArrayList<>());
        //thirdFilm.setGenres(new ArrayList<>(Arrays.asList(new Genre(2, "Драма"))));
        thirdFilm.setGenres(new ArrayList<>(Arrays.asList(new Genre(2, "Драма")))
        );
    }

    @Test
    public void shouldCreateUser() {
        firstUser = userStorage.createUser(firstUser);
        secondUser = userStorage.createUser(secondUser);
        Assertions.assertEquals("Name1", firstUser.getName(), "Поле name должно быть Name1");
        Assertions.assertEquals("Name2", secondUser.getName(), "Поле name должно быть Name2");
    }

    @Test
    public void shouldCheckUsersInStorage() {
        firstUser = userStorage.createUser(firstUser);
        secondUser = userStorage.createUser(secondUser);
        List<User> listUsers = userStorage.getUsers();
        assertThat(listUsers).contains(firstUser);
        assertThat(listUsers).contains(secondUser);
    }

    @Test
    public void shouldUpdateUser() {
        firstUser = userStorage.createUser(firstUser);
        firstUser.setName("updatedUser");
        userStorage.updateUser(firstUser);
        firstUser = userStorage.getUserById(firstUser.getId());

        Assertions.assertEquals("updatedUser", firstUser.getName(), "Поле name должно быть updated");
    }

    @Test
    public void shouldDeleteUserFromStorage() {
        firstUser = userStorage.createUser(firstUser);
        userStorage.deleteUser(firstUser.getId());
        List<User> listUsers = userStorage.getUsers();
        assertThat(listUsers).hasSize(0);
    }

    @Test
    public void shouldCreateFilm() {
        firstFilm = filmStorage.create(firstFilm);
        secondFilm = filmStorage.create(secondFilm);
        Assertions.assertEquals("Film1", firstFilm.getName(), "Поле name должно быть Film1");
        Assertions.assertEquals("Film2", secondFilm.getName(), "Поле name должно быть Film2");

    }

    @Test
    public void shouldCheckGetFilms() {
        firstFilm = filmStorage.create(firstFilm);
        secondFilm = filmStorage.create(secondFilm);
        thirdFilm = filmStorage.create(thirdFilm);
        List<Film> Films = filmStorage.getFilms();
        assertThat(Films).hasSize(3);

    }

 /*   @Test
    public void testUpdateFilm() {
        firstFilm = filmStorage.create(firstFilm);
        firstFilm.setName("New film");
        filmStorage.updateFilm(firstFilm);
        firstFilm = filmStorage.getFilmById(1l);
        Assertions.assertEquals("New film", firstFilm.getName());
    }*/

    @Test
    public void shouldDeleteFilm() {
        firstFilm = filmStorage.create(firstFilm);
        secondFilm = filmStorage.create(secondFilm);
        filmStorage.deleteFilm(firstFilm);
        List<Film> listFilms = filmStorage.getFilms();
        assertThat(listFilms).hasSize(1);
        assertThat(Optional.of(listFilms.get(0)))
                .hasValueSatisfying(film ->
                        AssertionsForClassTypes.assertThat(film)
                                .hasFieldOrPropertyWithValue("name", "Film2"));
    }

    @Test
    public void testAddLike() {
        firstUser = userStorage.createUser(firstUser);
        firstFilm = filmStorage.create(firstFilm);
        filmService.addLike(firstFilm.getId(), firstUser.getId());
        firstFilm = filmStorage.getFilmById(firstFilm.getId());
        assertThat(firstFilm.getLikes()).hasSize(1);
        assertThat(firstFilm.getLikes()).contains(firstUser.getId());
    }

    @Test
    public void testDeleteLike() {
        firstUser = userStorage.createUser(firstUser);
        secondUser = userStorage.createUser(secondUser);
        firstFilm = filmStorage.create(firstFilm);
        filmService.addLike(firstFilm.getId(), firstUser.getId());
        filmService.addLike(firstFilm.getId(), secondUser.getId());
        filmService.deleteLike(firstFilm.getId(), firstUser.getId());
        firstFilm = filmStorage.getFilmById(firstFilm.getId());
        assertThat(firstFilm.getLikes()).hasSize(1);
        assertThat(firstFilm.getLikes()).contains(secondUser.getId());
    }

    @Test
    public void testAddFriend() {
        firstUser = userStorage.createUser(firstUser);
        secondUser = userStorage.createUser(secondUser);
        userService.addFriend(firstUser.getId(), secondUser.getId());
        assertThat(userService.getFriends(firstUser.getId())).hasSize(1);
        assertThat(userService.getFriends(firstUser.getId())).contains(secondUser);
    }

    @Test
    public void testDeleteFriend() {
        firstUser = userStorage.createUser(firstUser);
        secondUser = userStorage.createUser(secondUser);
        thirdUser = userStorage.createUser(thirdUser);
        userService.addFriend(firstUser.getId(), secondUser.getId());
        userService.addFriend(firstUser.getId(), thirdUser.getId());
        userService.deleteFriend(firstUser.getId(), secondUser.getId());
        assertThat(userService.getFriends(firstUser.getId())).hasSize(1);
        assertThat(userService.getFriends(firstUser.getId())).contains(thirdUser);
    }

    @Test
    public void testGetFriends() {
        firstUser = userStorage.createUser(firstUser);
        secondUser = userStorage.createUser(secondUser);
        thirdUser = userStorage.createUser(thirdUser);
        userService.addFriend(firstUser.getId(), secondUser.getId());
        userService.addFriend(firstUser.getId(), thirdUser.getId());
        assertThat(userService.getFriends(firstUser.getId())).hasSize(2);
        assertThat(userService.getFriends(firstUser.getId())).contains(secondUser, thirdUser);
    }

    @Test
    public void testGetCommonFriends() {
        firstUser = userStorage.createUser(firstUser);
        secondUser = userStorage.createUser(secondUser);
        thirdUser = userStorage.createUser(thirdUser);
        userService.addFriend(firstUser.getId(), secondUser.getId());
        userService.addFriend(firstUser.getId(), thirdUser.getId());
        userService.addFriend(secondUser.getId(), firstUser.getId());
        userService.addFriend(secondUser.getId(), thirdUser.getId());
        assertThat(userService.getCommonFriends(firstUser.getId(), secondUser.getId())).hasSize(1);
        assertThat(userService.getCommonFriends(firstUser.getId(), secondUser.getId()))
                .contains(thirdUser);
    }
}
