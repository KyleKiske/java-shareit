package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.TestObjectMaker;
import ru.practicum.shareit.booking.*;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.request.Request;
import ru.practicum.shareit.request.RequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private RequestRepository requestRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private UserService userService;
    @Spy
    private ItemMapper itemMapper = Mappers.getMapper(ItemMapper.class);
    @Spy
    private CommentMapper commentMapper = Mappers.getMapper(CommentMapper.class);
    @Spy
    private BookingMapper bookingMapper = Mappers.getMapper(BookingMapper.class);
    @InjectMocks
    private ItemService itemService;

    @Test
    void addItem_expectUserNotFoundException() {
        long userId = 1;

        assertThrows(UserNotFoundException.class, () -> itemService.addItem(userId, null));
    }

    @Test
    void addItem_expectItemNoRequest() {
        long userId = 1;
        User user = TestObjectMaker.makeUser(userId);
        ItemCreateDto itemCreateDto = TestObjectMaker.makeItemDto(true);

        when(userService.getUserById(userId)).thenReturn(user);
        when(itemRepository.save(any())).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        ItemWithRequestDto item = itemService.addItem(userId, itemCreateDto);

        assertEquals(item.getOwner().getId(), 1L);
        assertEquals(item.getName(), itemCreateDto.getName());
        assertEquals(item.getAvailable(), itemCreateDto.getAvailable());
    }

    @Test
    void addItem_expectItemWithRequest() {
        long userId = 1;
        long requestId = 1;
        User user = TestObjectMaker.makeUser(userId);
        Request request = new Request();
        request.setCreated(LocalDateTime.now());
        request.setRequester(user);
        request.setId(requestId);
        ItemCreateDto itemCreateDto = TestObjectMaker.makeItemDto(true);
        itemCreateDto.setRequestId(requestId);

        when(userService.getUserById(userId)).thenReturn(user);
        when(requestRepository.findById(requestId)).thenReturn(Optional.of(request));
        when(itemRepository.save(any())).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        ItemWithRequestDto item = itemService.addItem(userId, itemCreateDto);

        assertEquals(item.getOwner().getId(), 1L);
        assertEquals(item.getName(), itemCreateDto.getName());
        assertEquals(item.getAvailable(), itemCreateDto.getAvailable());
        assertEquals(item.getRequestId(), requestId);
    }

    @Test
    void addItem_expectRequestNotFoundException() {
        long userId = 1;
        long requestId = 1;
        User user = TestObjectMaker.makeUser(userId);
        ItemCreateDto itemCreateDto = TestObjectMaker.makeItemDto(true);
        itemCreateDto.setRequestId(requestId);

        when(userService.getUserById(userId)).thenReturn(user);

        assertThrows(RequestNotFoundException.class, () -> itemService.addItem(userId, itemCreateDto));
    }

    @Test
    void redactItem_expectRedactedItem() {
        long userId = 1;
        long itemId = 1;
        User user = TestObjectMaker.makeUser(userId);
        Item item = TestObjectMaker.makeItem(itemId, user, true);
        ItemPatchDto itemPatchDto = new ItemPatchDto(itemId, "updateName", "UpdateDesc", false);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(itemRepository.save(any())).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        Item redactedItem = itemService.redactItem(userId, itemId, itemPatchDto);

        assertEquals(redactedItem.getOwner().getId(), userId);
        assertEquals(redactedItem.getName(), itemPatchDto.getName());
        assertEquals(redactedItem.getAvailable(), false);
        assertEquals(redactedItem.getDescription(), itemPatchDto.getDescription());
    }

    @Test
    void redactItem_expectWrongOwnerException() {
        long userId = 1;
        long itemId = 1;
        User user = TestObjectMaker.makeUser(userId);
        User userTwo = TestObjectMaker.makeUser(userId + 1);
        Item item = TestObjectMaker.makeItem(itemId, user, true);
        ItemPatchDto itemPatchDto = new ItemPatchDto(itemId, "updateName", "UpdateDesc", false);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        assertThrows(WrongOwnerException.class, () -> itemService.redactItem(userTwo.getId(), itemId, itemPatchDto));
    }

    @Test
    void getItemById() {
        long userId = 1;
        long itemId = 1;
        User user = TestObjectMaker.makeUser(userId);
        Item item = TestObjectMaker.makeItem(itemId, user, true);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.findAllByItemIdOrderByStartAsc(anyLong())).thenReturn(List.of());
        when(commentRepository.findAllByItemId(itemId)).thenReturn(List.of());

        Item itemFromGet = itemService.getItemById(itemId, userId);

        assertEquals(itemFromGet.getId(), item.getId());
        assertTrue(itemFromGet.getComments().isEmpty());
        assertNull(itemFromGet.getLastBooking());
        assertNull(itemFromGet.getNextBooking());
    }

    @Test
    void getItemByIdWithFutureBooking() {
        long userId = 1;
        long itemId = 1;
        User user = TestObjectMaker.makeUser(userId);
        User booker = TestObjectMaker.makeUser(userId + 1);
        Item item = TestObjectMaker.makeItem(itemId, user, true);
        Booking booking = new Booking(1L, LocalDateTime.now().plusHours(4), LocalDateTime.now().plusHours(6),
                BookingStatus.WAITING, item, booker);
        NotCurrentBooking notCurrentBooking = bookingMapper.bookingToNotCurrent(booking);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.findAllByItemIdOrderByStartAsc(anyLong())).thenReturn(List.of(booking));
        when(commentRepository.findAllByItemId(itemId)).thenReturn(List.of());

        Item itemFromGet = itemService.getItemById(itemId, userId);

        assertEquals(itemFromGet.getId(), item.getId());
        assertTrue(itemFromGet.getComments().isEmpty());
        assertNull(itemFromGet.getLastBooking());
        assertEquals(itemFromGet.getNextBooking(), notCurrentBooking);
    }

    @Test
    void searchItem_expectEmptyList() {
        assertTrue(itemService.searchItem("", PageRequest.of(0,1)).isEmpty());
    }

    @Test
    void searchItem_expectListOfItems() {
        List<Item> itemList = List.of(
                TestObjectMaker.makeItem(1, null, true),
                TestObjectMaker.makeItem(2, null, true),
                TestObjectMaker.makeItem(3, null, true));

        when(itemRepository.findAllByText(anyString(), any())).thenReturn(new PageImpl<>(itemList));
        assertEquals(itemService.searchItem("search", PageRequest.of(0,5)), new PageImpl<>(itemList));
    }

    @Test
    void getAllItemsOfUser_expectListOfUserItems() {
        long userId = 1L;
        int from = 5;
        int size = 5;
        List<Item> itemList = List.of(
                TestObjectMaker.makeItem(1, null, true),
                TestObjectMaker.makeItem(2, null, true),
                TestObjectMaker.makeItem(3, null, true));

        when(itemRepository.findAllByOwnerId(userId, PageRequest.of(from / size, size)))
                .thenReturn(new PageImpl<>(itemList));

        assertThat(itemService.getAllItemsOfUser(userId, PageRequest.of(from / size, size)).toList()).isEqualTo(itemList);
    }

    @Test
    void getAllItemsOfUser_expectIllegalArgumentException() {
        long userId = 1L;
        int from = -7;
        int size = 5;

        assertThrows(IllegalArgumentException.class, () -> itemService.getAllItemsOfUser(userId, PageRequest.of(from / size, size)));
    }

    @Test
    void deleteItem() {
        long itemId = 1;
        long userId = 1;
        User user = TestObjectMaker.makeUser(userId);
        Item item = TestObjectMaker.makeItem(itemId, user, true);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        itemService.deleteItem(itemId);
        verify(itemRepository).deleteById(userId);
    }

    @Test
    void addComment_Success() {
        long itemId = 1;
        long userId = 1;
        User booker = TestObjectMaker.makeUser(userId);
        User owner = TestObjectMaker.makeUser(userId);
        Item item = TestObjectMaker.makeItem(itemId, owner, true);
        CommentPostDto commentPostDto = new CommentPostDto("test text");

        when(userService.getUserById(userId)).thenReturn(booker);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(commentRepository.save(any())).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));
        when(bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(new PageImpl<>(List
                                .of(new Booking(1L, LocalDateTime.now(), LocalDateTime.now().plusDays(1),
                                BookingStatus.APPROVED, item, booker))));

        CommentDto commentDto = itemService.addComment(userId, itemId, commentPostDto);

        assertEquals(commentDto.getAuthorName(), booker.getName());
        assertEquals(commentDto.getText(), commentPostDto.getText());
    }

    @Test
    void addComment_expectUserIsNotBooker() {
        long itemId = 1;
        long userId = 1;
        User user = TestObjectMaker.makeUser(userId);
        User notBooker = TestObjectMaker.makeUser(2);
        Item item = TestObjectMaker.makeItem(itemId, user, true);
        CommentPostDto commentPostDto = new CommentPostDto("test text");

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(Page.empty());

        assertThrows(UserIsNotBookerException.class, () -> itemService.addComment(notBooker.getId(), itemId, commentPostDto));
    }

    @Test
    void addComment_expectItemNotFound() {
        long itemId = 1;
        User booker = TestObjectMaker.makeUser(2);
        CommentPostDto commentPostDto = new CommentPostDto("test text");

        assertThrows(ItemNotFoundException.class, () -> itemService.addComment(booker.getId(), itemId + 1, commentPostDto));
    }
}