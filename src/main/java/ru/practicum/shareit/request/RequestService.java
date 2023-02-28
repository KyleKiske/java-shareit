package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.RequestNotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestService {

    private final ItemRepository itemRepository;
    private final RequestRepository requestRepository;
    private final UserService userService;
    private final RequestMapper requestMapper;
    public final ItemMapper itemMapper;

    public RequestDto createRequest(long userId, RequestCreateDto requestCreateDto) {
        User user = userService.getUserById(userId);
        Request request = requestMapper.requestCreateDtoToRequest(requestCreateDto);
        request.setCreated(LocalDateTime.now());
        request.setRequester(user);
        return requestMapper.requestToRequestDto(requestRepository.save(request));
    }

    public List<RequestDto> getAllRequestInfoOfUser(long userId) {
        userService.getUserById(userId);
        List<Request> requestList = requestRepository.findAllByRequesterIdOrderByCreatedDesc(userId);

        List<RequestDto> requestDtoList = requestList.stream()
                .map(requestMapper::requestToRequestDto).collect(Collectors.toList());

        requestDtoList.forEach(
                requestDto -> requestDto.setItems(
                        itemRepository.findAllByRequestId(requestDto.getId()).stream()
                                .map(itemMapper::itemToRequestDto).collect(Collectors.toList())));
        return requestDtoList;
    }

    public List<RequestDto> getAllRequestOfOtherUsers(long userId, PageRequest pageRequest) {
        userService.getUserById(userId);
        List<Request> requestList = requestRepository.findAllByRequesterIdIsNotOrderByCreatedDesc(
                userId, pageRequest);

        List<RequestDto> requestDtoList = requestList.stream()
                .map(requestMapper::requestToRequestDto).collect(Collectors.toList());

        requestDtoList.forEach(
                requestDto -> requestDto.setItems(
                        itemRepository.findAllByRequestId(requestDto.getId()).stream()
                                .map(itemMapper::itemToRequestDto).collect(Collectors.toList())));

        return requestDtoList;
    }

    public RequestDto getRequestInfo(long userId, long requestId) {
        userService.getUserById(userId);
        Request request = requestRepository.findById(requestId).orElseThrow(() -> new RequestNotFoundException(String.valueOf(requestId)));
        RequestDto requestDto = requestMapper.requestToRequestDto(request);
        requestDto.setItems(itemRepository.findAllByRequestId(requestId).stream()
                .map(itemMapper::itemToRequestDto)
                .collect(Collectors.toList()));
        return requestDto;
    }
}
