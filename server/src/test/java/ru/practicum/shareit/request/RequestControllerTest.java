package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.TestObjectMaker;
import ru.practicum.shareit.exception.ErrorHandler;
import ru.practicum.shareit.request.dto.RequestCreateDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class RequestControllerTest {

    @Mock
    private RequestService requestService;
    @InjectMocks
    private RequestController requestController;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    private final RequestMapper requestMapper = new RequestMapper();
    private MockMvc mvc;


    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(requestController)
                .setControllerAdvice(ErrorHandler.class)
                .build();
    }

    @Test
    void createRequest() throws Exception {
        long userId = 1;
        Request request = new Request(1L, "", null, LocalDateTime.now());
        RequestCreateDto requestCreateDto = new RequestCreateDto("Test request");
        RequestDto requestDto = requestMapper.requestToRequestDto(request);
        requestDto.setDescription(requestCreateDto.getDescription());

        when(requestService.createRequest(1, requestCreateDto)).thenReturn(requestDto);

        mvc.perform(post("/requests").header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestCreateDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(requestDto)));
    }

    @Test
    void getAllRequestOfRequester() throws Exception {
        long userId = 1;
        User user = TestObjectMaker.makeUser(userId);
        List<Request> requestList = List.of(
                new Request(1L, "s", user, LocalDateTime.now()),
                new Request(2L, "w", user, LocalDateTime.now())
        );
        List<RequestDto> requestDtoList = requestList.stream()
                .map(requestMapper::requestToRequestDto).collect(Collectors.toList());

        when(requestService.getAllRequestInfoOfUser(userId)).thenReturn(requestDtoList);

        mvc.perform(get("/requests").header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(requestDtoList)));
    }


    @Test
    void getRequestsOfOtherUsers() throws Exception {
        long userId = 1;
        int from = 5;
        int size = 5;
        User user = TestObjectMaker.makeUser(userId);
        User user2 = TestObjectMaker.makeUser(2);
        List<Request> requestList = List.of(
                new Request(1L, "s", user, LocalDateTime.now()),
                new Request(2L, "w", user, LocalDateTime.now())
        );
        List<RequestDto> requestDtoList = requestList.stream()
                .map(requestMapper::requestToRequestDto).collect(Collectors.toList());

        when(requestService.getAllRequestOfOtherUsers(user2.getId(), PageRequest.of(from / size, size)))
                .thenReturn(new PageImpl<>(requestDtoList));

        mvc.perform(get("/requests/all").header("X-Sharer-User-Id", user2.getId())
                        .param("from", String.valueOf(from))
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(requestDtoList)));
    }

    @Test
    void getRequestInfo() throws Exception {
        Request request = new Request(1L, "", null, LocalDateTime.now());
        RequestDto requestDto = requestMapper.requestToRequestDto(request);

        when(requestService.getRequestInfo(1, 1)).thenReturn(requestDto);

        mvc.perform(get("/requests/{requestId}", 1)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(requestDto)));
    }
}