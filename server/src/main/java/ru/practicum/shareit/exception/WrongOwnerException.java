package ru.practicum.shareit.exception;

public class WrongOwnerException extends RuntimeException {
    public WrongOwnerException(final String message) {
        super(message);
    }
}
