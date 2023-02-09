package ru.yandex.practicum.item;

import lombok.Value;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Value
public class ItemDto {
    @NotBlank(message = "Имя не указано")
    String name;
    @NotBlank(message = "Описание не может быть пустым")
    String description;
    @NotNull(message = "Доступность должна быть указана")
    Boolean available;
}
