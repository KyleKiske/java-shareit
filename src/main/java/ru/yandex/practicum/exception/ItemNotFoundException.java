package ru.yandex.practicum.exception;

public class ItemNotFoundException extends RuntimeException {
    public ItemNotFoundException(final String message){
        super(message);
    }
}
