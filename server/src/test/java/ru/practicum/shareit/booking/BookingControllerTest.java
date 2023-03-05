package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.TestObjectMaker;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.ErrorHandler;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {

    @Mock
    private BookingService bookingService;

    @InjectMocks
    private BookingController bookingController;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(bookingController)
                .setControllerAdvice(ErrorHandler.class)
                .build();
    }



    @Test
    void createBooking_expectCreate() throws Exception {
        long userId = 1;
        long itemId = 1;
        Booking booking = TestObjectMaker.makeBooking(1, null, null);
        BookingDto bookingDto = TestObjectMaker.makeBookingDto(itemId);

        when(bookingService.addBooking(1, bookingDto)).thenReturn(booking);

        mvc.perform(post("/bookings").header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDto)))
                .andExpect(status().isCreated());
    }

    @Test
    void changeBookingStatus() throws Exception {
        long userId = 1;
        long bookingId = 1;
        Booking booking = TestObjectMaker.makeBooking(1, null, null);
        booking.setStatus(BookingStatus.APPROVED);

        when(bookingService.changeBookingStatus(userId, bookingId, true)).thenReturn(booking);

        mvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(booking)));
    }

    @Test
    void getBookingInfo() throws Exception {
        long userId = 1;
        long bookingId = 1;
        Booking booking = TestObjectMaker.makeBooking(1, null, null);

        when(bookingService.getBookingInfo(userId, bookingId)).thenReturn(booking);

        mvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(booking)));
    }

    @Test
    void getBookingsOfBooker() throws Exception {
        long userId = 1;
        List<Booking> bookingList = List.of(
                TestObjectMaker.makeBooking(1, null, null),
                TestObjectMaker.makeBooking(2, null, null),
                TestObjectMaker.makeBooking(3, null, null)
        );

        when(bookingService.getBookingsByBooker(anyLong(), anyString(), any())).thenReturn(new PageImpl<>(bookingList));

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", "ALL")
                        .param("size", "5")
                        .param("from", "0"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(bookingList)));
    }

    @Test
    void getBookingOfOwner() throws Exception {
        long userId = 1;
        List<Booking> bookingList = List.of(
                TestObjectMaker.makeBooking(1, null, null),
                TestObjectMaker.makeBooking(2, null, null),
                TestObjectMaker.makeBooking(3, null, null)
        );

        when(bookingService.getBookingsByOwner(anyLong(), anyString(), any())).thenReturn(new PageImpl<>(bookingList));

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", "ALL")
                        .param("size", "5")
                        .param("from", "0"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(bookingList)));
    }
}