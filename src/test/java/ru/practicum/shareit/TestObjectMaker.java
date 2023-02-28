package ru.practicum.shareit;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingDto;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemCreateDto;
import ru.practicum.shareit.item.ItemWithRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;

import java.time.LocalDateTime;

public class TestObjectMaker {

    public static User makeUser(long id) {
        User user = new User();
        user.setId(id);
        user.setName("TestName");
        user.setEmail("test@email.com" + id);
        return user;
    }

    public static UserDto makeUserDto(String email) {
        UserDto user = new UserDto();
        user.setName("TestName");
        user.setEmail(email);
        return user;
    }

    public static Item makeItem(long id, User user, boolean available) {
        Item item = new Item();
        item.setId(id);
        item.setName("TestName");
        item.setDescription("Test Description");
        item.setAvailable(available);
        item.setOwner(user);
        return item;
    }

    public static ItemCreateDto makeItemDto(boolean available) {
        ItemCreateDto item = new ItemCreateDto();
        item.setName("TestName");
        item.setDescription("Test Description");
        item.setAvailable(available);
        return item;
    }

    public static ItemWithRequestDto makeItemRequestDto(boolean available) {
        ItemWithRequestDto item = new ItemWithRequestDto();
        item.setName("TestName");
        item.setDescription("Test Description");
        item.setAvailable(available);
        item.setRequestId(null);
        return item;
    }

    public static Booking makeBooking(long id, User user, Item item) {
        Booking booking = new Booking();
        booking.setId(id);
        booking.setStart(LocalDateTime.now().plusHours(2));
        booking.setEnd(LocalDateTime.now().plusHours(4));
        booking.setStatus(BookingStatus.WAITING);
        booking.setItem(item);
        booking.setBooker(user);
        return booking;
    }

    public static BookingDto makeBookingDto(long itemId) {
        BookingDto booking = new BookingDto();
        booking.setItemId(itemId);
        booking.setStart(LocalDateTime.now().plusHours(2));
        booking.setEnd(LocalDateTime.now().plusHours(4));
        return booking;
    }

}
