package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.PaginationMaker;
import ru.practicum.shareit.request.dto.RequestCreateDto;
import ru.practicum.shareit.request.dto.RequestDto;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class RequestController {

    private final RequestService requestService;

    @PostMapping
    public RequestDto createRequest(@RequestHeader ("X-Sharer-User-Id") long userId,
                                    @RequestBody RequestCreateDto requestCreateDto) {
        return requestService.createRequest(userId, requestCreateDto);
    }

    @GetMapping
    public List<RequestDto> getAllRequestOfRequester(@RequestHeader("X-Sharer-User-Id") long userId) {
        return requestService.getAllRequestInfoOfUser(userId);
    }

    @GetMapping("/all")
    public List<RequestDto> getRequestsOfOtherUsers(@RequestHeader("X-Sharer-User-Id") long userId,
                                                 @RequestParam(required = false) Integer from,
                                                 @RequestParam(required = false) Integer size) {
        return requestService.getAllRequestOfOtherUsers(userId, PaginationMaker.makePageRequest(from, size)).toList();
    }

    @GetMapping("/{requestId}")
    public RequestDto getRequestInfo(@RequestHeader("X-Sharer-User-Id") long userId,
                                     @PathVariable long requestId) {
        return requestService.getRequestInfo(userId, requestId);
    }
}
