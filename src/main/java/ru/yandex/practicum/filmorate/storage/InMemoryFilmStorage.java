package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private long idFilm = 1L;
    private final Map<Long, Film> storage = new HashMap<>();

    @Override
    public Film create(Film film) {
        if (validateFilm(film)) {
            film.setId(idFilm);
            storage.put(idFilm++, film);
            log.info("Текущее количество фильмов: {}", storage.size());
        }
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (storage.containsKey(film.getId())) {
            storage.put(film.getId(), film);
        } else {
            throw new NotFoundException("Нет такого ID: " + film.getId());
        }
        return film;
    }

    @Override
    public void deleteFilm(Film film) {
        //validateFilm(film);
        if (storage.containsKey(film.getId())) {
            storage.remove(film.getId());
            log.debug("Пользователь {} удалён", film);
        } else {
            throw new NotFoundException("Нет такого ID: " + film.getId());
        }
    }

    @Override
    public Film getFilmById(Long id) {
        if (storage.containsKey(id)) {
            return storage.get(id);
        } else {
            throw new NotFoundException("Нет такого ID: " + id);
        }
    }

    public List<Film> getFilms() {
        List<Film> filmList = new ArrayList<>(storage.values());
        log.info("Текущее количество фильмов: {}", filmList.size());
        return filmList;
    }

    private boolean validateFilm(Film film) {
        if (film.getName().isEmpty()) {
            throw new ValidationException("Название фильма не должно быть пустым.");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года.");
        }

        if (film.getDescription().length() > 200) {
            throw new ValidationException("Максимальная длина описания — 200 символов.");
        }

        if (film.getDuration() <= 0) {
            throw new ValidationException("Продолжительность фильма должна быть положительной.");
        }
        return true;
    }
}
