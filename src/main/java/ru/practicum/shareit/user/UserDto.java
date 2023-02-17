package ru.practicum.shareit.user;

import lombok.Value;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Value
public class UserDto {
    @NotBlank(message = "Имя пользователя не указано")
    String name;
    @Email(message = "Неправильный email")
    @NotBlank(message = "Email не указан")
    String email;
}
