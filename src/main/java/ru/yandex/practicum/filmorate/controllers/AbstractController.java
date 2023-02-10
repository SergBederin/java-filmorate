package ru.yandex.practicum.filmorate.controllers;

import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.AbstractModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractController<T extends AbstractModel> {
    private long id = 0L;
    private final Map<Long, T> storage = new HashMap<>();

    public T create(final T data) {
        validate(data);
        data.setId(++id);
        storage.put(data.getId(), data);
        return data;
    }

    public T update(final T data) {
        if (storage.containsKey(data.getId())) {
            storage.put(data.getId(), data);
        } else {
            throw new ValidationException("Нет такого ID: " + data.getId());
        }
        return data;
    }

    public List<T> allData() {
        List<T> dataList = (List<T>) new ArrayList<>(storage.values());
        return (List<T>) dataList;
    }


    abstract void validate(T data);

}
