package ru.practicum.shareit;

import org.springframework.data.domain.PageRequest;

public class PaginationMaker {
    public static PageRequest makePageRequest(Integer from, Integer size) {
        if (from == null || size == null) {
            return null;
        } else if (size <= 0 || from < 0) {
            if (size <= 0) {
                throw new IllegalArgumentException("Размер страницы должен быть больше 0");
            } else {
                throw new IllegalArgumentException("Номер первого элемента должен быть больше 0");
            }

        } else {
            int page = from / size;
            return PageRequest.of(page, size);
        }
    }
}
