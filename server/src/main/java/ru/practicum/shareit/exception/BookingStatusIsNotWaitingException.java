package ru.practicum.shareit.exception;

public class BookingStatusIsNotWaitingException extends RuntimeException {
    public BookingStatusIsNotWaitingException(final String message) {
        super(message);
    }
}
