package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.PaginationMaker;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Booking createBooking(@RequestHeader(name = "X-Sharer-User-Id") long userId,
                                 @RequestBody BookingDto bookingDto) {
        return bookingService.addBooking(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public Booking changeBookingStatus(@RequestHeader(name = "X-Sharer-User-Id") long ownerId,
                                       @PathVariable long bookingId,
                                       @RequestParam boolean approved) {
        return bookingService.changeBookingStatus(ownerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public Booking getBookingInfo(@RequestHeader(name = "X-Sharer-User-Id") long userId,
                                  @PathVariable long bookingId) {
        return bookingService.getBookingInfo(userId, bookingId);
    }

    @GetMapping
    public List<Booking> getBookingsOfBooker(@RequestHeader(name = "X-Sharer-User-Id") long userId,
                                             @RequestParam(defaultValue = "ALL",  name = "state") String state,
                                             @RequestParam(required = false) Integer from,
                                             @RequestParam(required = false) Integer size) {
        return bookingService.getBookingsByBooker(userId, state, PaginationMaker.makePageRequest(from, size)).toList();
    }

    @GetMapping("/owner")
    public List<Booking> getBookingOfOwner(@RequestHeader(name = "X-Sharer-User-Id") long userId,
                                           @RequestParam(defaultValue = "ALL", name = "state") String state,
                                           @RequestParam(required = false) Integer from,
                                           @RequestParam(required = false) Integer size) {
        return bookingService.getBookingsByOwner(userId, state, PaginationMaker.makePageRequest(from, size)).toList();
    }

}
