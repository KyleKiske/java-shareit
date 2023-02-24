package ru.practicum.shareit.exception;

public class UserIsNotBookerException extends RuntimeException {
    public UserIsNotBookerException(final String message) {
        super(message);
    }
}
