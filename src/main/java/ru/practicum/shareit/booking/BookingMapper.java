package ru.practicum.shareit.booking;

import org.mapstruct.Mapper;

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

    public NotCurrentBooking bookingToNotCurrent(Booking booking1) {
        if (booking1 == null) {
            return null;
        }
        return new NotCurrentBooking(booking1.getId(), booking1.getBooker().getId());
    }
}
