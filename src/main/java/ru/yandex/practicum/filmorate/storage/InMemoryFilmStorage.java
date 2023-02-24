package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

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
        if (FilmService.validateFilm(film)) {
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

        if (!storage.containsKey(film.getId())) {
            throw new NotFoundException("Нет такого ID: " + film.getId());
        }
        storage.remove(film.getId());
        log.debug("Пользователь {} удалён", film);
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

}
