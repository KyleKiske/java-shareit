package ru.practicum.shareit.booking;

import lombok.Value;

import java.time.LocalDateTime;

@Value
public class NotCurrentBooking {
    long id;
    long bookerId;
    LocalDateTime start;
    LocalDateTime end;
}
