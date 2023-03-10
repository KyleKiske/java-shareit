package ru.practicum.shareit.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.ItemWithRequestDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class RequestDto {
    private Long id;
    private String description;
    private LocalDateTime created;
    private List<ItemWithRequestDto> items;
}
