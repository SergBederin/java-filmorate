package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    Film create(Film film);

    Film updateFilm(Film film);

    void deleteFilm(Film film);

    Film getFilmById(Long id);

    List<Film> getFilms();

    void addLike(Long filmId, Long userId);

    void deleteLike(Long filmId, Long userId);
}
