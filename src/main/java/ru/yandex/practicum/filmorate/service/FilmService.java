package ru.yandex.practicum.filmorate.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    @Getter
    private final UserStorage userStorage;

    public Film create(Film film) {
        if (validateFilm(film)) {
            filmStorage.create(film);
        }
        return film;
    }

    public Film updateFilm(Film film) {

        return filmStorage.updateFilm(film);
    }

    public List<Film> getFilms() {
        return filmStorage.getFilms();
    }

    public Film getFilmById(Long id) {
        return filmStorage.getFilmById(id);
    }

    public void addLike(Long filmId, Long userId) {
        userStorage.getUserById(userId);
        Film film = filmStorage.getFilmById(filmId);
        film.getLikes().add(userId);
    }

    public void deleteLike(Long filmId, Long userId) {
        Film film = filmStorage.getFilmById(filmId);
        Set<Long> likes = film.getLikes();
        userStorage.getUserById(userId);
        if (likes.contains(userId)) {
            likes.remove(userId);
            film.setLikes(likes);
        } else {
            throw new NotFoundException("Пользователь с ID: " + userId + " не ставил лайк фильму");
        }
    }

    public List<Film> getPopularFilms(Integer filmQuantity) {
        Comparator<Film> filmLikeComparator = (film1, film2) -> {
            if (film1.getLikes().size() == film2.getLikes().size()) {
                return (int) (film1.getId() - film2.getId());
            } else {
                return film1.getLikes().size() - film2.getLikes().size();
            }
        };
        Set<Film> popularFilms = new TreeSet<>(filmLikeComparator.reversed());
        List<Film> films = filmStorage.getFilms();
        popularFilms.addAll(films);
        if (Objects.isNull(filmQuantity)) {
            filmQuantity = 10;
        }
        return popularFilms.stream().limit(filmQuantity).collect(Collectors.toList());
    }

    public  boolean validateFilm(Film film) {
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
