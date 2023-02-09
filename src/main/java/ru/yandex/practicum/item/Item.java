package ru.yandex.practicum.item;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Item {
    Long id;
    String name;
    String description;
    Boolean available;
    long owner;
    String request;

}