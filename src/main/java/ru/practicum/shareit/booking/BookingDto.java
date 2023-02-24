package ru.practicum.shareit.booking;

import lombok.Value;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Value
public class BookingDto {
    @NotNull(message = "Вещь не выбрана")
    Long itemId;

    @NotNull(message = "Дата начала аренды не указаны")
    LocalDateTime start;

    @NotNull(message = "Дата конца аренды не указаны")
    LocalDateTime end;
}
