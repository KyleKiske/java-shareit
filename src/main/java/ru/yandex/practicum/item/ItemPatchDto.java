package ru.yandex.practicum.item;

import lombok.Value;

@Value
public class ItemPatchDto {
    long id;
    String name;
    String description;
    Boolean available;
}
