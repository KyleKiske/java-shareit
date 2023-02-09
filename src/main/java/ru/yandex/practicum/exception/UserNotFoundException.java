package ru.yandex.practicum.exception;

public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException(final String message){
        super(message);
    }
}
