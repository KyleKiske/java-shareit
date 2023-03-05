package ru.practicum.shareit.request;

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
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.exception.RequestNotFoundException;
import ru.practicum.shareit.item.CommentMapper;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.dto.RequestCreateDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RequestServiceTest {
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private RequestRepository requestRepository;
    @Mock
    private UserService userService;
    @Spy
    private RequestMapper requestMapper = Mappers.getMapper(RequestMapper.class);
    @Spy
    private CommentMapper commentMapper = Mappers.getMapper(CommentMapper.class);
    @Spy
    private BookingMapper bookingMapper = Mappers.getMapper(BookingMapper.class);
    @Spy
    private ItemMapper itemMapper = Mappers.getMapper(ItemMapper.class);
    @InjectMocks
    private RequestService requestService;

    @Test
    void createRequest() {
        long userId = 1;
        User user = TestObjectMaker.makeUser(userId);
        RequestCreateDto requestCreateDto = new RequestCreateDto();
        requestCreateDto.setDescription("Help me, demons!");

        when(userService.getUserById(userId)).thenReturn(user);
        when(requestRepository.save(any())).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        RequestDto request = requestService.createRequest(userId, requestCreateDto);

        assertNotNull(request.getCreated());
        assertEquals(request.getDescription(), requestCreateDto.getDescription());
        assertThat(request.getCreated().isBefore(LocalDateTime.now()));
    }

    @Test
    void getAllRequestInfoOfUser() {
        long userId = 1;
        User user = TestObjectMaker.makeUser(userId);
        List<Request> requestList = List.of(
                new Request(1L, "Help me, demons!", user, LocalDateTime.now()),
                new Request(2L, "test2", user, LocalDateTime.now())
        );

        when(userService.getUserById(userId)).thenReturn(user);
        when(itemRepository.findAllByRequestIdIsIn(anyList())).thenReturn(List.of());
        when(requestRepository.findAllByRequesterIdOrderByCreatedDesc(userId)).thenReturn(requestList);

        assertEquals(requestService.getAllRequestInfoOfUser(userId), requestList.stream()
                .map(requestMapper::requestToRequestDto)
                .peek(requestDto -> requestDto.setItems(List.of()))
                .collect(Collectors.toList()));
    }

    @Test
    void getAllRequestOfOtherUsers() {
        long userId = 1;
        int from = 5;
        int size = 5;
        User user = TestObjectMaker.makeUser(userId);
        User user2 = TestObjectMaker.makeUser(2);
        List<Request> requestList = List.of(
                new Request(1L, "Help me, demons!", user, LocalDateTime.now()),
                new Request(2L, "test2", user, LocalDateTime.now())
        );

        when(userService.getUserById(user2.getId())).thenReturn(user2);
        when(requestRepository.findAllByRequesterIdIsNotOrderByCreatedDesc(
                user2.getId(),
                PageRequest.of(from / size, size)))
                .thenReturn(new PageImpl<>(requestList));

        assertEquals(requestService.getAllRequestOfOtherUsers(
                    user2.getId(), PageRequest.of(from / size, size)).toList(), requestList.stream()
                .map(requestMapper::requestToRequestDto)
                .peek(requestDto -> requestDto.setItems(List.of()))
                .collect(Collectors.toList()));
    }

    @Test
    void getRequestInfo() {
        long requestId = 1;
        long userId = 1;
        User user = TestObjectMaker.makeUser(userId);
        Request request = new Request(1L, "Help me, demons!", user, LocalDateTime.now());
        RequestDto requestDto = requestMapper.requestToRequestDto(request);
        requestDto.setItems(Collections.emptyList());

        when(itemRepository.findAllByRequestId(anyLong())).thenReturn(Collections.emptyList());
        when(requestRepository.findById(anyLong())).thenReturn(Optional.of(request));

        assertThat(requestService.getRequestInfo(userId, requestId)).isEqualTo(requestDto);
    }

    @Test
    void getRequestInfo_expectRequestNotFoundException() {
        long userId = 1;
        User user = TestObjectMaker.makeUser(userId);
        Request request = new Request(1L, "Help me, demons!", user, LocalDateTime.now());
        RequestDto requestDto = requestMapper.requestToRequestDto(request);
        requestDto.setItems(Collections.emptyList());

        when(requestRepository.findById(anyLong())).thenThrow(new RequestNotFoundException("5"));

        assertThrows(RequestNotFoundException.class, () -> requestService.getRequestInfo(userId, 5));
    }

}