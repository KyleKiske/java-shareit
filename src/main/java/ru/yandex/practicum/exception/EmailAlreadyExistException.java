package ru.yandex.practicum.exception;

public class EmailAlreadyExistException extends RuntimeException{
    public EmailAlreadyExistException(final String message){
        super(message);
    }
}
