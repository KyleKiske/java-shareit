package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
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

        if (!item.getAvailable()) {
            throw new ItemNotAvailableException(item.getId().toString());
        }

        if (item.getOwner().getId().equals(booker.getId())) {
            throw new WrongUserException("Нельзя бронировать вещи у себя самого");
        }

        boolean endBeforeStart = booking.getEnd().isBefore(booking.getStart());
        boolean startBeforeNow = booking.getStart().isBefore(LocalDateTime.now());

        if (startBeforeNow) {
            throw new BadDateException(
                    "Дата начала бронирования раньше текущего времени." + booking.getStart().toString());
        } else if (endBeforeStart) {
            throw new BadDateException(
                    "Дата начала аренды позже даты окончания\n"
                            + booking.getStart().toString() + "\n"
                            + booking.getEnd().toString());
        }

        booking.setStatus(BookingStatus.WAITING);
        booking.setBooker(booker);
        booking.setItem(item);

        return bookingRepository.save(booking);
    }

    public Booking changeBookingStatus(long userId, long bookingId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException(String.valueOf(bookingId)));

        if (booking.getItem().getOwner().getId() != userId) {
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

    public List<Booking> getBookingsByOwner(long userId, String state, PageRequest pageRequest) {
        userService.getUserById(userId);
        switch (state) {
            case "ALL":
                return bookingRepository.findAllByItemOwnerIdOrderByStartDesc(
                        userId,
                        pageRequest);
            case "CURRENT":
                return bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                        userId,
                        LocalDateTime.now(),
                        LocalDateTime.now(),
                        pageRequest);
            case "PAST":
                return bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(
                        userId,
                        LocalDateTime.now(),
                        pageRequest);
            case "FUTURE":
                return bookingRepository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(
                        userId,
                        LocalDateTime.now(),
                        pageRequest);
            case "WAITING":
                return bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(
                        userId,
                        BookingStatus.WAITING,
                        pageRequest);
            case "REJECTED":
                return bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(
                        userId,
                        BookingStatus.REJECTED,
                        pageRequest);
            default:
                throw new UnsupportedStateException("Unknown state: " + state);
        }
    }

    public List<Booking> getBookingsByBooker(long bookerId, String state, PageRequest pageRequest) {
        userService.getUserById(bookerId);
        switch (state) {
            case "ALL":
                return bookingRepository.findAllByBookerIdOrderByStartDesc(
                        bookerId,
                        pageRequest);
            case "CURRENT":
                return bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                        bookerId,
                        LocalDateTime.now(),
                        LocalDateTime.now(),
                        pageRequest);
            case "PAST":
                return bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(
                        bookerId,
                        LocalDateTime.now(),
                        pageRequest);
            case "FUTURE":
                return bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(
                        bookerId,
                        LocalDateTime.now(),
                        pageRequest);
            case "WAITING":
                return bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(
                        bookerId,
                        BookingStatus.WAITING,
                        pageRequest);
            case "REJECTED":
                return bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(
                        bookerId,
                        BookingStatus.REJECTED,
                        pageRequest);
            default:
                throw new UnsupportedStateException("Unknown state: " + state);
        }
    }
}
