package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestCreateDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
@Validated
public class RequestController {

    private final RequestClient requestClient;

    private static final String SHARER_USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> createRequest(@RequestHeader (SHARER_USER_ID) long userId,
                                                @RequestBody @Valid RequestCreateDto requestCreateDto) {
        return requestClient.createRequest(userId, requestCreateDto);
    }

    @GetMapping
    public ResponseEntity<Object> getAllRequestOfRequester(@RequestHeader(SHARER_USER_ID) long userId) {
        return requestClient.getAllRequestOfUser(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getRequestsOfOtherUsers(
            @RequestHeader(SHARER_USER_ID) long userId,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return requestClient.getAllRequestOfOtherUsers(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestInfo(
                    @RequestHeader(SHARER_USER_ID) long userId,
                    @PathVariable long requestId) {
        return requestClient.getRequestInfo(userId, requestId);
    }
}
