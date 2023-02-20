package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final ItemService itemService;
    private final UserService userService;

    public Booking addBooking(long userId, BookingDto bookingDto) {
        User booker = userService.getUserById(userId);
        Item item = itemService.getItemById(bookingDto.getItemId(), userId);

        Booking booking = bookingMapper.dtoToBooking(bookingDto);

        if (item.getOwner().getId().equals(booker.getId())) {
            throw new WrongUserException("Нельзя бронировать вещи у себя самого");
        }

        boolean endBeforeStart = booking.getEnd().isBefore(booking.getStart());
        boolean startBeforeNow = booking.getStart().isBefore(LocalDateTime.now());

        if (startBeforeNow || endBeforeStart) {
            throw new BadDateException(booking.getStart().toString());
        }

        if (item.getAvailable()) {
            booking.setStatus(BookingStatus.WAITING);
            booking.setBooker(booker);
            booking.setItem(item);
        } else {
            throw new ItemNotAvailableException(item.getId().toString());
        }

        System.out.println(booking);
        return bookingRepository.save(booking);
    }

    public Booking changeBookingStatus(long userId, long bookingId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException(String.valueOf(bookingId)));

        if (booking.getItem().getOwner().getId() != (userId)) {
            throw new WrongUserException("У вас нет доступа к данному бронированию");
        }
        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new BookingStatusIsNotWaitingException(String.valueOf(bookingId));
        }

        BookingStatus bookingStatus = approved ? BookingStatus.APPROVED : BookingStatus.REJECTED;
        booking.setStatus(bookingStatus);
        return bookingRepository.save(booking);
    }

    public Booking getBookingInfo(long userId, long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException(String.valueOf(bookingId)));
        if ((booking.getBooker().getId() != (userId)) && (booking.getItem().getOwner().getId() != userId)) {
            throw new WrongUserException("У вас нет доступа к данному бронированию");
        }
        return booking;
    }

    public List<Booking> getBookingsByOwner(long userId, String state) {
        userService.getUserById(userId);
        switch (state) {
            case "ALL":
                return bookingRepository.findAllByItemOwnerIdOrderByStartDesc(userId);
            case "CURRENT":
                return bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, LocalDateTime.now(), LocalDateTime.now());
            case "PAST":
                return bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now());
            case "FUTURE":
                return bookingRepository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now());
            case "WAITING":
                return bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
            case "REJECTED":
                return bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
            default:
                throw new UnsupportedStateException("Unknown state: " + state);
        }
    }

    public List<Booking> getBookingsByBooker(long bookerId, String state) {
        userService.getUserById(bookerId);
        System.out.println(state);
        switch (state) {
            case "ALL":
                return bookingRepository.findAllByBookerIdOrderByStartDesc(bookerId);
            case "CURRENT":
                return bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(bookerId, LocalDateTime.now(), LocalDateTime.now());
            case "PAST":
                return bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(bookerId, LocalDateTime.now());
            case "FUTURE":
                return bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(bookerId, LocalDateTime.now());
            case "WAITING":
                return bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(bookerId, BookingStatus.WAITING);
            case "REJECTED":
                return bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(bookerId, BookingStatus.REJECTED);
            default:
                throw new UnsupportedStateException("Unknown state: " + state);
        }
    }
}
