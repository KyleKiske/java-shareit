package ru.practicum.shareit.booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    Page<Booking> findAllByBookerIdOrderByStartDesc(long bookerId, Pageable pageable);

    Page<Booking> findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(long bookerId,
                                                                             LocalDateTime start,
                                                                             LocalDateTime end,
                                                                             Pageable pageable);

    Page<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(long bookerId, LocalDateTime start, Pageable pageable);

    Page<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(long bookerId, LocalDateTime end, Pageable pageable);

    Page<Booking> findAllByBookerIdAndStatusOrderByStartDesc(long bookerId, BookingStatus bookingStatus, Pageable pageable);

    Page<Booking> findAllByItemOwnerIdOrderByStartDesc(long ownerId, Pageable pageable);

    Page<Booking> findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(long ownerId,
                                                                                LocalDateTime start,
                                                                                LocalDateTime end,
                                                                                Pageable pageable);

    Page<Booking> findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(long ownerId, LocalDateTime end, Pageable pageable);

    Page<Booking> findAllByItemOwnerIdAndStartAfterOrderByStartDesc(long ownerId,
                                                                    LocalDateTime start,
                                                                    Pageable pageable);

    Page<Booking> findAllByItemOwnerIdAndStatusOrderByStartDesc(long ownerId,
                                                                BookingStatus bookingStatus,
                                                                Pageable pageable);

    List<Booking> findAllByItemIdOrderByStartAsc(long itemId);

}