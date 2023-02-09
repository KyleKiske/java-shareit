package ru.yandex.practicum.user;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class User {
    Long id;
    String name;
    String email;
}