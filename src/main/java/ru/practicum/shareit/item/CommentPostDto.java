package ru.practicum.shareit.item;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CommentPostDto {
    @NotBlank(message = "Текст комментария не может быть пустым")
    private String text;
}
