package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
	private final BookingClient bookingClient;
	private static final String SHARER_USER_ID = "X-Sharer-User-Id";

	@PostMapping
	public ResponseEntity<Object> bookItem(
						@RequestHeader(SHARER_USER_ID) long userId,
						@RequestBody @Valid BookItemRequestDto requestDto) {
		if (requestDto.getEnd().isBefore(requestDto.getStart())) {
			throw new IllegalArgumentException("End of the booking is before start");
		}
		log.info("Creating booking {}, userId={}", requestDto, userId);
		return bookingClient.bookItem(userId, requestDto);
	}

	@PatchMapping("/{bookingId}")
	public ResponseEntity<Object> changeBookingStatus(@RequestHeader(SHARER_USER_ID) long ownerId,
													  @PathVariable long bookingId,
													  @RequestParam(required = false) boolean approved) {
		log.info("Change state of booking {}, userId={}", bookingId, ownerId);
		return bookingClient.changeBookingStatus(ownerId, bookingId, approved);
	}

	@GetMapping("/{bookingId}")
	public ResponseEntity<Object> getBooking(@RequestHeader(SHARER_USER_ID) long userId,
											 @PathVariable Long bookingId) {
		log.info("Get booking {}, userId={}", bookingId, userId);
		return bookingClient.getBooking(userId, bookingId);
	}

	@GetMapping
	public ResponseEntity<Object> getBookingsOfBooker(
			@RequestHeader(SHARER_USER_ID) long userId,
			@RequestParam(name = "state", defaultValue = "all") String state,
			@PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
			@Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
		BookingState bookingState = BookingState.from(state)
				.orElseThrow(() -> new IllegalArgumentException("Unknown state: " + state));
		log.info("Get booking with state {}, userId={}, from={}, size={}", state, userId, from, size);
		return bookingClient.getBookingsOfBooker(userId, bookingState, from, size);
	}

	@GetMapping("/owner")
	public ResponseEntity<Object> getBookingsOfOwner(
			@RequestHeader(SHARER_USER_ID) long userId,
			@RequestParam(name = "state", defaultValue = "all") String state,
			@PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
			@Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
		BookingState bookingState = BookingState.from(state)
				.orElseThrow(() -> new IllegalArgumentException("Unknown state: " + state));
		return bookingClient.getBookingsOfOwner(userId, bookingState, from, size);
	}
}