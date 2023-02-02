package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controllers.FilmController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class FilmControllerTest {
    static FilmController filmController = new FilmController();

    @Test
    void shouldValidateFilmEmptyName() {
        Film film = new Film();
        Exception exception = assertThrows(NullPointerException.class, () -> filmController.createFilm(film));
        assertNull(exception.getMessage());

        film.setName("");
        exception = assertThrows(NullPointerException.class, () -> filmController.createFilm(film));
        assertNull(exception.getMessage());
    }

    @Test
    void shouldValidateDescriptionFilmTest() {
        Film film = new Film();
        film.setName("Кинофильм");
        film.setReleaseDate(LocalDate.of(2023, 01, 01));
        film.setDescription(("Замечательный фильм замечательный фильм замечательный фильм замечательный фильм замечательный фильм замечательный фильм замечательный фильм замечательный фильм " +
                "замечательный фильм замечательный фильм замечательный фильм "));
        Exception exception = assertThrows(ValidationException.class, () -> filmController.createFilm(film));
        assertEquals("Максимальная длина описания — 200 символов.", exception.getMessage());
    }

    @Test
    void shouldValidateFilmReleaseDate() {
        Film film = new Film();
        film.setName("Кинофильм");
        film.setDescription(("Замечательный фильм"));
        film.setDuration(60);
        film.setReleaseDate(LocalDate.of(1800, 01, 01));
        Exception exception = assertThrows(ValidationException.class, () -> filmController.createFilm(film));
        assertEquals("Дата релиза не может быть раньше 28 декабря 1895 года.", exception.getMessage());
    }

    @Test
    void shouldValidateFilmDuration() {
        Film film = new Film();
        film.setName("Кинофильм");
        film.setDescription("Замечательный фильм");
        film.setReleaseDate(LocalDate.of(2023, 01, 01));
        film.setDuration(-60);
        Exception exception = assertThrows(ValidationException.class, () -> filmController.createFilm(film));
        assertEquals("Продолжительность фильма должна быть положительной.", exception.getMessage());
    }
}
