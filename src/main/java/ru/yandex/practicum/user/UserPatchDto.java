package ru.yandex.practicum.user;

import lombok.Value;

import javax.validation.constraints.Email;

@Value
public class UserPatchDto {
    String name;
    @Email(message = "Неправильный email")
    String email;
}
