package ru.practicum.shareit.booking;

import lombok.Value;

@Value
public class NotCurrentBooking {
    long id;
    long bookerId;
}
