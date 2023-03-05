package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.NotCurrentBooking;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.user.User;

import java.util.List;

@Data
@NoArgsConstructor
public class ItemWithRequestDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long requestId;
    private User owner;
    private NotCurrentBooking lastBooking;
    private NotCurrentBooking nextBooking;
    private List<CommentDto> comments;
}
