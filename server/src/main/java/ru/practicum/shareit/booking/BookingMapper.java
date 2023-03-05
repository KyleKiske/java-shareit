package ru.practicum.shareit.booking;

import org.mapstruct.Mapper;
import ru.practicum.shareit.booking.dto.BookingDto;

@Mapper(componentModel = "spring")
public class BookingMapper {
    public Booking dtoToBooking(BookingDto dto) {
        if (dto == null) {
            return null;
        }
        Booking booking = new Booking();
        booking.setStart(dto.getStart());
        booking.setEnd(dto.getEnd());
        return booking;
    }

    public NotCurrentBooking bookingToNotCurrent(Booking booking) {
        if (booking == null) {
            return null;
        }
        return new NotCurrentBooking(booking.getId(), booking.getBooker().getId(), booking.getStart(), booking.getEnd());
    }
}
