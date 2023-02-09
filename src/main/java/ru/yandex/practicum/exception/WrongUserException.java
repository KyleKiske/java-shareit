package ru.yandex.practicum.exception;

public class WrongUserException extends RuntimeException{
    public WrongUserException(final String message){
        super(message);
    }

}
