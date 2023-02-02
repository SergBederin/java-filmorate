package ru.yandex.practicum.filmorate.controllers;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class FilmController {
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);
    private final Map<Long, Film> films = new HashMap<>();
    private Long idFilm = 0L;
    private static final LocalDate FILM_BIRTHDAY = LocalDate.of(1895, 12, 28);

    @PostMapping(value = "/films")
    public Film createFilm(@Valid @RequestBody Film film) {
        log.info("Отправлен запрос Post:/films");
        validateFilm(film);
        idFilm++;
        film.setId(idFilm);
        films.put(film.getId(), film);
        log.debug("Добавлен фильм: {}", film.getName());
        return film;
    }

    @PutMapping(value = "/films")
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.info("Отправлен запрос Put:/films");
        validateFilm(film);
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            log.debug("Фильм {} обновлен.", film.getName());
        } else {
            throw new ValidationException("Нет фильма с таким ID.");
        }
        return film;
    }

    @GetMapping("/films")
    public List<Film> getFilms() {
        log.info("Отправлен запрос GET:/films");
        List<Film> filmList = new ArrayList<>(films.values());
        log.debug("Всего фильмов: {}", films.size());
        return filmList;
    }
    private void validateFilm(Film film) {
        if (film.getReleaseDate().isBefore(FILM_BIRTHDAY)) {
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года.");
        }

        if (film.getDescription().length() > 200) {
            throw new ValidationException("Максимальная длина описания — 200 символов.");
        }

        if (film.getDuration() <= 0) {
            throw new ValidationException("Продолжительность фильма должна быть положительной.");
        }
    }

}


