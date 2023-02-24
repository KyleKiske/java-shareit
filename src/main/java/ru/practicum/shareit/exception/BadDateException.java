package ru.practicum.shareit.exception;

public class BadDateException extends RuntimeException {
    public BadDateException(final String message) {
        super(message);
    }
}
