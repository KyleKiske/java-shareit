package ru.yandex.practicum.exception;

public class EmptyEmailException extends RuntimeException{
    public EmptyEmailException(final String message){
        super(message);
    }
}