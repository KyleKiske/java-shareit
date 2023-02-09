package ru.practicum.shareit.exception;

public class WrongUserException extends RuntimeException {
    public WrongUserException(final String message) {
        super(message);
    }

}
