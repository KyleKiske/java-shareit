package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.RequestNotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemWithRequestDto;
import ru.practicum.shareit.request.dto.RequestCreateDto;
import ru.practicum.shareit.request.dto.RequestDto;
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

        List<ItemWithRequestDto> allLinkedItems = itemRepository.findAllByRequestIdIsIn(requestList.stream()
                .map(Request::getId)
                .collect(Collectors.toList())).stream().map(itemMapper::itemToRequestDto).collect(Collectors.toList());

        requestDtoList.forEach(requestDto -> requestDto.setItems(allLinkedItems.stream()
                .filter(item -> item.getRequestId().equals(requestDto.getId()))
                .collect(Collectors.toList())));

        return requestDtoList;
    }

    public Page<RequestDto> getAllRequestOfOtherUsers(long userId, PageRequest pageRequest) {
        userService.getUserById(userId);
        Page<Request> requestList = requestRepository.findAllByRequesterIdIsNotOrderByCreatedDesc(
                userId, pageRequest);

        List<RequestDto> requestDtoList = requestList.stream()
                .map(requestMapper::requestToRequestDto).collect(Collectors.toList());

        List<ItemWithRequestDto> allLinkedItems = itemRepository.findAllByRequestIdIsIn(requestList.stream()
                .map(Request::getId)
                .collect(Collectors.toList())).stream().map(itemMapper::itemToRequestDto).collect(Collectors.toList());

        requestDtoList.forEach(requestDto -> requestDto.setItems(allLinkedItems.stream()
                .filter(item -> item.getRequestId().equals(requestDto.getId()))
                .collect(Collectors.toList())));

        return new PageImpl<>(requestDtoList);
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
