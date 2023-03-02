package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.TestObjectMaker;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.*;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemService itemService;
    @Mock
    private UserService userService;
    @Spy
    private ItemMapper itemMapper = Mappers.getMapper(ItemMapper.class);
    @Spy
    private CommentMapper commentMapper = Mappers.getMapper(CommentMapper.class);
    @Spy
    private BookingMapper bookingMapper = Mappers.getMapper(BookingMapper.class);
    @InjectMocks
    private BookingService bookingService;

    @Test
    void addBooking_expectUserNotFoundException() {
        long userId = 1;
        long itemId = 1;
        BookingDto bookingDto = new BookingDto(itemId, LocalDateTime.now(), LocalDateTime.now().plusHours(1));

        when(userService.getUserById(userId)).thenThrow(new UserNotFoundException(String.valueOf(userId)));

        assertThrows(UserNotFoundException.class, () -> bookingService.addBooking(userId, bookingDto));
    }

    @Test
    void addBooking_expectItemNotFoundException() {
        long userId = 1;
        long itemId = 1;
        BookingDto bookingDto = new BookingDto(itemId, LocalDateTime.now(), LocalDateTime.now().plusHours(1));

        when(itemService.getItemById(itemId, userId)).thenThrow(new ItemNotFoundException(String.valueOf(itemId)));

        assertThrows(ItemNotFoundException.class, () -> bookingService.addBooking(userId, bookingDto));
    }

    @Test
    void addBooking_expectWrongUserException() {
        long userId = 1;
        long itemId = 1;
        User user = TestObjectMaker.makeUser(userId);
        Item item = TestObjectMaker.makeItem(itemId, user, true);
        BookingDto bookingDto = new BookingDto(itemId, LocalDateTime.now(), LocalDateTime.now().plusHours(1));

        when(userService.getUserById(userId)).thenReturn(user);
        when(itemService.getItemById(itemId, userId)).thenReturn(item);

        assertThrows(WrongUserException.class, () -> bookingService.addBooking(userId, bookingDto));
    }

    @Test
    void addBooking_expectItemNotAvailable() {
        long userId = 1;
        long itemId = 1;
        User user = TestObjectMaker.makeUser(userId);
        Item item = TestObjectMaker.makeItem(itemId, user, false);
        BookingDto bookingDto = new BookingDto(itemId, LocalDateTime.now(), LocalDateTime.now().plusHours(1));

        when(itemService.getItemById(itemId, userId)).thenReturn(item);

        assertThrows(ItemNotAvailableException.class, () -> bookingService.addBooking(userId, bookingDto));
    }

    @Test
    void addBooking_expectBadDateExceptionStartBeforeNow() {
        long userId = 1;
        long itemId = 1;
        User booker = TestObjectMaker.makeUser(1);
        User owner = TestObjectMaker.makeUser(2);
        Item item = TestObjectMaker.makeItem(itemId, owner, true);
        BookingDto bookingDto = new BookingDto(itemId, LocalDateTime.now().minusDays(2), LocalDateTime.now().plusHours(3));

        when(userService.getUserById(userId)).thenReturn(booker);
        when(itemService.getItemById(itemId, userId)).thenReturn(item);

        assertThrows(BadDateException.class, () -> bookingService.addBooking(userId, bookingDto));
    }

    @Test
    void addBooking_expectBadDateExceptionEndBeforeStart() {
        long userId = 1;
        long itemId = 1;
        User booker = TestObjectMaker.makeUser(1);
        User owner = TestObjectMaker.makeUser(2);
        Item item = TestObjectMaker.makeItem(itemId, owner, true);
        BookingDto bookingDto = new BookingDto(itemId, LocalDateTime.now().plusHours(2), LocalDateTime.now().plusHours(1));

        when(userService.getUserById(userId)).thenReturn(booker);
        when(itemService.getItemById(itemId, userId)).thenReturn(item);

        assertThrows(BadDateException.class, () -> bookingService.addBooking(userId, bookingDto));
    }

    @Test
    void addBooking_expectCreated() {
        long userId = 1;
        long itemId = 1;
        BookingDto dto = new BookingDto(itemId, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));
        User booker = TestObjectMaker.makeUser(1);
        User owner = TestObjectMaker.makeUser(2);
        Item item = TestObjectMaker.makeItem(itemId, owner, true);

        when(userService.getUserById(userId)).thenReturn(booker);
        when(itemService.getItemById(itemId, userId)).thenReturn(item);
        when(bookingRepository.save(any())).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        Booking booking = bookingService.addBooking(userId, dto);

        assertEquals(booking.getStart(), dto.getStart());
        assertEquals(booking.getEnd(), dto.getEnd());
        assertEquals(booking.getBooker().getId(), userId);

        assertThat(booking).hasFieldOrProperty("id");
    }

    @Test
    void changeBookingStatus_expectApproved() {
        long bookingId = 1;
        long userId = 1;
        long itemId = 1;
        User user = TestObjectMaker.makeUser(userId);
        Item item = TestObjectMaker.makeItem(itemId, user, true);
        Booking booking = new Booking(bookingId, LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2), BookingStatus.WAITING, item, user);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any())).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        booking = bookingService.changeBookingStatus(userId, bookingId, true);

        assertThat(booking.getStatus()).isEqualTo(BookingStatus.APPROVED);
    }

    @Test
    void changeBookingStatus_expectRejected() {
        long bookingId = 1;
        long userId = 1;
        long itemId = 1;
        User user = TestObjectMaker.makeUser(userId);
        Item item = TestObjectMaker.makeItem(itemId, user, true);
        Booking booking = new Booking(bookingId, LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2), BookingStatus.WAITING, item, user);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any())).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        booking = bookingService.changeBookingStatus(userId, bookingId, false);

        assertThat(booking.getStatus()).isEqualTo(BookingStatus.REJECTED);
    }

    @Test
    void changeBookingStatus_expectBookingStatusIsNotWaiting() {
        long bookingId = 1;
        long userId = 1;
        long itemId = 1;
        User user = TestObjectMaker.makeUser(userId);
        Item item = TestObjectMaker.makeItem(itemId, user, true);
        Booking booking = new Booking(bookingId, LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2), BookingStatus.CANCELED, item, user);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThrows(BookingStatusIsNotWaitingException.class, () ->
                bookingService.changeBookingStatus(userId, bookingId, true));

    }

    @Test
    void getBookingInfo_expectBooking() {
        long bookingId = 1;
        long userId = 1;
        long itemId = 1;
        User user = TestObjectMaker.makeUser(userId);
        Item item = TestObjectMaker.makeItem(itemId, user, true);
        Booking booking = new Booking(bookingId, LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2), BookingStatus.WAITING, item, user);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThat(bookingService.getBookingInfo(userId, bookingId)).isEqualTo(booking);
    }

    @Test
    void getBookingInfo_expectBookingNotFoundException() {
        long userId = 1;

        when(bookingRepository.findById(5L)).thenThrow(new BookingNotFoundException(""));

        assertThrows(BookingNotFoundException.class, () -> bookingService.getBookingInfo(userId, 5L));
    }

    @Test
    void getBookingInfo_expectWrongUserException() {
        long bookingId = 1;
        long userId = 1;
        long itemId = 1;
        User user = TestObjectMaker.makeUser(userId);
        Item item = TestObjectMaker.makeItem(itemId, user, true);
        Booking booking = new Booking(bookingId, LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2), BookingStatus.WAITING, item, user);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThrows(WrongUserException.class, () -> bookingService.getBookingInfo(3, bookingId));
    }

    @Test
    void getBookingsByOwner_expectBookingsWithAnyStatus() {
        long userId = 1;
        long itemId = 1;
        int from = 5;
        int size = 5;
        User user = TestObjectMaker.makeUser(userId);
        Item item = TestObjectMaker.makeItem(itemId, user, true);
        List<Booking> bookingList = List.of(
                TestObjectMaker.makeBooking(1,user,item),
                TestObjectMaker.makeBooking(2,user,item),
                TestObjectMaker.makeBooking(3,user,item)
        );

        when(bookingRepository.findAllByItemOwnerIdOrderByStartDesc(
                userId,
                PageRequest.of(from / size, size))).thenReturn(new PageImpl<>(bookingList));

        assertEquals(bookingList, bookingService
                .getBookingsByOwner(userId, "ALL", PageRequest.of(from / size, size)).toList());
    }


    @Test
    void getBookingsByOwner_expectCallFindAllByItemOwnerIdOrderByStartDesc() {
        bookingService.getBookingsByOwner(1, "ALL", PageRequest.of(1, 5));
        verify(bookingRepository).findAllByItemOwnerIdOrderByStartDesc(anyLong(), any());
    }

    @Test
    void getBookingsByOwner_expectCallFindAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc() {
        bookingService.getBookingsByOwner(1, "CURRENT", PageRequest.of(1, 5));
        verify(bookingRepository)
                .findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(anyLong(), any(), any(), any());
    }

    @Test
    void getBookingsByOwner_expectCallFindAllByItemOwnerIdAndEndBeforeOrderByStartDesc() {
        bookingService.getBookingsByOwner(1, "PAST", PageRequest.of(1, 5));
        verify(bookingRepository)
                .findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(anyLong(), any(), any());
    }

    @Test
    void getBookingsByOwner_expectCallFindAllByItemOwnerIdAndStartAfterOrderByStartDesc() {
        bookingService.getBookingsByOwner(1, "FUTURE", PageRequest.of(1, 5));
        verify(bookingRepository)
                .findAllByItemOwnerIdAndStartAfterOrderByStartDesc(anyLong(), any(), any());
    }

    @Test
    void getBookingsByOwner_expectCallFindAllByItemOwnerIdAndStatusOrderByStartDescWithStatusWaiting() {
        bookingService.getBookingsByOwner(1, "WAITING", PageRequest.of(1, 5));
        verify(bookingRepository)
                .findAllByItemOwnerIdAndStatusOrderByStartDesc(1, BookingStatus.WAITING, PageRequest.of(1, 5));
    }

    @Test
    void getBookingsByOwner_expectCallFindAllByItemOwnerIdAndStatusOrderByStartDescWithStatusRejected() {
        bookingService.getBookingsByOwner(1, "REJECTED", PageRequest.of(1, 5));
        verify(bookingRepository)
                .findAllByItemOwnerIdAndStatusOrderByStartDesc(1, BookingStatus.REJECTED, PageRequest.of(1, 5));
    }

    @Test
    void getBookingsByOwner_expectUnsupportedStateException() {
        long userId = 1;
        assertThrows(UnsupportedStateException.class, () ->
                bookingService.getBookingsByOwner(userId, "UNSUPPORTED", PageRequest.of(1, 5)));
    }

    @Test
    void getBookingsByBooker_success() {
        long userId = 1;
        long itemId = 1;
        User user = TestObjectMaker.makeUser(userId);
        Item item = TestObjectMaker.makeItem(itemId, user, true);
        List<Booking> bookingList = List.of(
                TestObjectMaker.makeBooking(1,user,item),
                TestObjectMaker.makeBooking(2,user,item),
                TestObjectMaker.makeBooking(3,user,item)
        );

        when(bookingRepository.findAllByBookerIdOrderByStartDesc(
                userId,
                PageRequest.of(1, 5))).thenReturn(new PageImpl<>(bookingList));

        assertEquals(bookingList, bookingService
                .getBookingsByBooker(userId, "ALL", PageRequest.of(1, 5)).toList());
    }

    @Test
    void getBookingsByBooker_expectCallFindAllByBookerIdOrderByStartDesc() {
        bookingService.getBookingsByBooker(1, "ALL", PageRequest.of(1, 5));
        verify(bookingRepository).findAllByBookerIdOrderByStartDesc(anyLong(), any());
    }

    @Test
    void getBookingsByBooker_expectCallFindAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc() {
        bookingService.getBookingsByBooker(1, "CURRENT", PageRequest.of(1, 5));
        verify(bookingRepository)
                .findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(anyLong(), any(), any(), any());
    }

    @Test
    void getBookingsByBooker_expectCallFindAllByBookerIdAndEndBeforeOrderByStartDesc() {
        bookingService.getBookingsByBooker(1, "PAST", PageRequest.of(1, 5));
        verify(bookingRepository)
                .findAllByBookerIdAndEndBeforeOrderByStartDesc(anyLong(), any(), any());
    }

    @Test
    void getBookingsByBooker_expectCallFindAllByBookerIdAndStartAfterOrderByStartDesc() {
        bookingService.getBookingsByBooker(1, "FUTURE", PageRequest.of(1, 5));
        verify(bookingRepository)
                .findAllByBookerIdAndStartAfterOrderByStartDesc(anyLong(), any(), any());
    }

    @Test
    void getBookingsByBooker_expectCallFindAllByBookerIdAndStatusOrderByStartDescWithStatusWaiting() {
        bookingService.getBookingsByBooker(1, "WAITING", PageRequest.of(1, 5));
        verify(bookingRepository)
                .findAllByBookerIdAndStatusOrderByStartDesc(1, BookingStatus.WAITING, PageRequest.of(1, 5));
    }

    @Test
    void getBookingsByBooker_expectCallFindAllByBookerIdAndStatusOrderByStartDescWithStatusRejected() {
        bookingService.getBookingsByBooker(1, "REJECTED", PageRequest.of(1, 5));
        verify(bookingRepository)
                .findAllByBookerIdAndStatusOrderByStartDesc(1, BookingStatus.REJECTED, PageRequest.of(1, 5));
    }

    @Test
    void getBookingsByBooker_expectUnsupportedStateException() {
        long userId = 1;
        assertThrows(UnsupportedStateException.class, () ->
                bookingService.getBookingsByBooker(userId, "UNSUPPORTED", PageRequest.of(1, 5)));
    }
}