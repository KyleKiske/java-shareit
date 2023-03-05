package ru.practicum.shareit.exception;

public class ItemNotAvailableException extends RuntimeException {
    public ItemNotAvailableException(final String message) {
        super(message);
    }
}
