package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.TestObjectMaker;
import ru.practicum.shareit.exception.ErrorHandler;
import ru.practicum.shareit.exception.UserNotFoundException;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private MockMvc mvc;


    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(userController)
                .setControllerAdvice(ErrorHandler.class)
                .build();
    }

    @Test
    void createUser_expectCreatedUser() throws Exception {
        User user = TestObjectMaker.makeUser(1);
        UserDto userDto = new UserDto(user.getName(), user.getEmail());

        when(userService.createUser(userDto)).thenReturn(user);

        String result = mvc.perform(post("/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(userDto))
                    .characterEncoding(StandardCharsets.UTF_8)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(user), result);
    }

    @Test
    void getUserById_expectUser() throws Exception {
        List<User> users = List.of(
                TestObjectMaker.makeUser(1),
                TestObjectMaker.makeUser(2),
                TestObjectMaker.makeUser(3));

        when(userService.getUserById(1L)).thenReturn(users.get(0));

        mvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(users.get(0))));
    }

    @Test
    void getUserById_expectUserNotFound() throws Exception {
        long userId = 1L;

        when(userService.getUserById(userId)).thenThrow(new UserNotFoundException(String.valueOf(userId)));

        mvc.perform(get("/users/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getUserById_expectInternalServerError() throws Exception {
        mvc.perform(get("/users/wrongId"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @SneakyThrows
    void getAllUsers_expectListOfUsers() {
        List<User> users = List.of(
                TestObjectMaker.makeUser(1),
                TestObjectMaker.makeUser(2),
                TestObjectMaker.makeUser(3));

        when(userService.getAllUsers()).thenReturn(users);

        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("TestName")))
                .andExpect(jsonPath("$[0].email", is("test@email.com1")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[2].id", is(3)));
    }

    @Test
    @SneakyThrows
    void getAllUsers_expectEmpty() {
        List<User> users = List.of();

        when(userService.getAllUsers()).thenReturn(users);

        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void redactUserInfo_expectUpdatedUser() throws Exception {
        long userId = 1;
        User user = TestObjectMaker.makeUser(userId);
        UserPatchDto dto = new UserPatchDto("user.getName()", user.getEmail());
        User redactedUser = new User(userId, dto.getEmail(), dto.getName());

        when(userService.redactUser(userId, dto)).thenReturn(redactedUser);

        mvc.perform(patch("/users/" + userId).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(redactedUser)));
    }

    @Test
    void redactUserInfo_expectThrowNotFound() throws Exception {
        long userId = 1;
        User user = TestObjectMaker.makeUser(userId);
        UserPatchDto dto = new UserPatchDto("user.getName()", user.getEmail());

        when(userService.redactUser(userId, dto)).thenThrow(new UserNotFoundException(String.valueOf(userId)));

        mvc.perform(patch("/users/" + userId).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteUserById_expectSuccess() throws Exception {
        long userId = 1;
        User user = TestObjectMaker.makeUser(userId);

        when(userService.deleteUser(userId)).thenReturn(user);

        mvc.perform(delete("/users/" + userId))
                .andExpect(status().isOk());
    }

    @Test
    void deleteUserById_expectUserNotFound() throws Exception {
        long userId = 1;

        when(userService.deleteUser(userId)).thenThrow(new UserNotFoundException(""));

        mvc.perform(delete("/users/" + userId))
                .andExpect(status().isNotFound());
    }
}