package ru.yandex.practicum.filmorate.service;

import lombok.Getter;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FilmService {
    @Getter
    private final FilmStorage filmStorage;

    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public void addLike(Long filmId, Long userId) {
        Film film = filmStorage.getFilmById(filmId);
        Set<Long> likes = film.getLikes();
        likes.add(userId);
        film.setLikes(likes);
        filmStorage.updateFilm(film);
    }

    public void deleteLike(Long filmId, Long userId) {
        Film film = filmStorage.getFilmById(filmId);
        Set<Long> likes = film.getLikes();
        if (likes.contains(userId)) {
            likes.remove(userId);
            film.setLikes(likes);
            filmStorage.updateFilm(film);
        } else {
            throw new NotFoundException("Пользователь с ID: " + userId + "не ставил лайк фильму");
        }
    }

    public Set<Film> getPopularFilms(Integer filmQuantity) {
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
        return popularFilms.stream().limit(filmQuantity).collect(Collectors.toSet());
    }
}
