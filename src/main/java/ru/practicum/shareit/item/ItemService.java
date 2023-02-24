package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UserIsNotBookerException;
import ru.practicum.shareit.exception.WrongOwnerException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemMapper itemMapper;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final BookingMapper bookingMapper;
    private final CommentMapper commentMapper;
    private final UserService userService;
    private final CommentService commentService;

    public Item addItem(long userId, ItemDto itemDto) {
        User user = userService.getUserById(userId);
        Item item = itemMapper.dtoToItem(itemDto);
        item.setOwner(user);
        return itemRepository.save(item);
    }

    public Item redactItem(long userId, long itemId, ItemPatchDto itemPatchDto) {
        Item itemFromRepo = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(String.valueOf(itemId)));
        if (itemFromRepo.getOwner().getId() != userId) {
            throw new WrongOwnerException("У вас нет доступа к данной вещи.");
        }
        if (itemPatchDto.getName() != null) {
            itemFromRepo.setName(itemPatchDto.getName());
        }
        if (itemPatchDto.getDescription() != null) {
            itemFromRepo.setDescription(itemPatchDto.getDescription());
        }
        if (itemPatchDto.getAvailable() != null) {
            itemFromRepo.setAvailable(itemPatchDto.getAvailable());
        }
        return itemRepository.save(itemFromRepo);
    }

    public Item getItemById(long itemId, long userId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException(String.valueOf(itemId)));

        if (item.getOwner().getId() == userId) {
            List<Booking> bookings = bookingRepository.findAllByItemIdOrderByStartAsc(itemId);

            item.setNextBooking(bookingMapper.bookingToNotCurrent(getNextBooking(bookings)));
            item.setLastBooking(bookingMapper.bookingToNotCurrent(getLastBooking(bookings)));
        }

        item.setComments(commentRepository.findAllByItemId(item.getId()).stream()
                .map(commentMapper::commentToDto)
                .collect(Collectors.toList()));

        return item;
    }

    public List<Item> searchItem(String text) {
        if (text.isBlank()) {
            return List.of();
        }
        return itemRepository.findAllByText(text);
    }

    public List<Item> getAllItemsOfUser(Long userId) {
        List<Item> itemList = itemRepository.getAllByOwnerId(userId);
        for (Item item: itemList) {
            List<Booking> bookings = bookingRepository.findAllByItemIdOrderByStartAsc(item.getId());

            item.setNextBooking(bookingMapper.bookingToNotCurrent(getNextBooking(bookings)));
            item.setLastBooking(bookingMapper.bookingToNotCurrent(getLastBooking(bookings)));

            item.setComments(commentRepository.findAllByItemId(item.getId()).stream()
                    .map(commentMapper::commentToDto)
                    .collect(Collectors.toList()));
        }
        return itemList;
    }

    public void deleteItem(long itemId) {
        itemRepository.deleteById(itemId);
    }

    public CommentDto addComment(long userId, long itemId, CommentPostDto commentPost) {
        User user = userService.getUserById(userId);
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException(String.valueOf(itemId)));

        List<Booking> bookings = bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now());

        if (bookings.isEmpty()) {
            throw new UserIsNotBookerException(String.valueOf(userId));
        }
        Comment comment = new Comment();

        comment.setText(commentPost.getText());
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreated(LocalDateTime.now());

        return commentMapper.commentToDto(commentRepository.save(comment));
    }

    private Booking getNextBooking(List<Booking> bookings) {
        List<Booking> sortedBookings = bookings.stream()
                .filter(booking -> booking.getStart().isAfter(LocalDateTime.now()))
                .collect(Collectors.toList());

        return sortedBookings.isEmpty() ? null : sortedBookings.get(0);
    }

    private Booking getLastBooking(List<Booking> bookings) {
        List<Booking> sortedBookings = bookings.stream()
                .filter(booking -> booking.getEnd().isBefore(LocalDateTime.now()))
                .collect(Collectors.toList());

        return sortedBookings.isEmpty() ? null : sortedBookings.get(sortedBookings.size() - 1);
    }
}
