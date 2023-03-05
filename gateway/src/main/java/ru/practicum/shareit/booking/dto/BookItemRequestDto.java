package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BookItemRequestDto {
	private long itemId;
	@NotNull(message = "Booking start date should be specified")
	@FutureOrPresent(message = "Booking start date is before current time")
	private LocalDateTime start;
	@NotNull(message = "Booking end date should be specified")
	@Future(message = "Booking end date is before current time")
	private LocalDateTime end;
}
