package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ItemCreateDto {
    String name;
    String description;
    Boolean available;
    Long requestId;
}
