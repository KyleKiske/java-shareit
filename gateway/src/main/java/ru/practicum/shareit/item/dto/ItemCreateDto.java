package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class ItemCreateDto {
    @NotBlank(message = "Name is not stated")
    String name;
    @NotBlank(message = "Description in required")
    String description;
    @NotNull(message = "Availability is required")
    Boolean available;
    Long requestId;
}
