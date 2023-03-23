package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.filmorate.controllers.FilmController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.*;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmControllerTest {

    private Film film;
    private static Validator validator;
    private FilmController filmController;
    private FilmStorage filmStorage;
    private UserStorage userStorage;
    private GenreDbStorage genreStorage;

    @BeforeAll
    public static void init() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @BeforeEach
    public void beforeEach() {

        film = new Film();
        filmStorage = new InMemoryFilmStorage();
        userStorage = new InMemoryUserStorage();
        // filmController = new FilmController(new FilmService(filmStorage, userStorage));
        film.setName("Кинофильм");
        film.setDescription("Замечательный фильм");
        film.setReleaseDate(LocalDate.of(2023, 01, 01));
        film.setDuration(90);
    }

    @Test
    void shouldValidateFilmEmptyName() {
        film.setName("");
        Set<ConstraintViolation<Film>> validate = validator.validate(film);
        Set<String> errorMessages = validate.stream().map(ConstraintViolation::getMessage).collect(Collectors.toSet());
        assertEquals(1, errorMessages.size());
    }

    @Test
    void shouldValidateDescriptionFilmTest() {
        film.setDescription(("Замечательный фильм замечательный фильм замечательный фильм замечательный фильм замечательный фильм замечательный фильм замечательный фильм замечательный фильм " +
                "замечательный фильм замечательный фильм замечательный фильм "));
        Set<ConstraintViolation<Film>> validate = validator.validate(film);
        Set<String> errorMessages = validate.stream().map(ConstraintViolation::getMessage).collect(Collectors.toSet());
        assertEquals(1, errorMessages.size());
    }

  /*  @Test
    void dateValidationTest() {
        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        Set<ConstraintViolation<Film>> validate = validator.validate(film);
        Exception exception = assertThrows(ValidationException.class, () -> filmController.createFilm(film));
        assertEquals("Дата релиза не может быть раньше 28 декабря 1895 года.", exception.getMessage());
    }*/

    @Test
    void shouldValidateFilmDuration() {
        film.setDuration(0);
        Set<ConstraintViolation<Film>> validate = validator.validate(film);
        Set<String> errorMessages = validate.stream().map(ConstraintViolation::getMessage).collect(Collectors.toSet());
        assertEquals(1, errorMessages.size());
    }
}

