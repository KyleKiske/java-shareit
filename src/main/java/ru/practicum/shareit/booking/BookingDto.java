package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingDto {
    @NotNull(message = "Вещь не выбрана")
    Long itemId;

    @NotNull(message = "Дата начала аренды не указаны")
    LocalDateTime start;

    @NotNull(message = "Дата конца аренды не указаны")
    LocalDateTime end;
}
