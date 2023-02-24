package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        log.info("Отправлен запрос Post:/films");
        return filmService.getFilmStorage().create(film);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.info("Отправлен запрос Put:/films");
        return filmService.getFilmStorage().updateFilm(film);
    }

    @GetMapping
    public List<Film> getFilms() {
        log.info("Отправлен запрос GET:/films");
        return filmService.getFilmStorage().getFilms();
    }

    @GetMapping("/{id}")
    public Optional<Film> getFilmById(@PathVariable Long id) {
        log.info("Отправлен запрос GET:/films");
        return Optional.ofNullable(filmService.getFilmStorage().getFilmById(id));
    }

    @PutMapping(value = "/{id}/like/{userId}")
    public void addLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Отправлен запрос GET:/films/{id}/like/{userId}");
        filmService.addLike(id, userId);
        log.info("пользователь поставил лайк фильму");
    }

    @DeleteMapping(value = "/{id}/like/{userId}")
    public void deleteLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Отправлен запрос DELETE:/films/{id}/like/{userId}");
        filmService.deleteLike(id, userId);
        log.info("пользователь удалил лайк к фильму");
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(required = false) Integer count) {
        return filmService.getPopularFilms(count);
    }

}


