package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.TestObjectMaker;
import ru.practicum.shareit.exception.ErrorHandler;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ItemControllerTest {

    private MockMvc mvc;
    @Mock
    private ItemService itemService;

    @InjectMocks
    private ItemController itemController;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(itemController)
                .setControllerAdvice(ErrorHandler.class)
                .build();
    }

    @Test
    void getItemsOfUser_exceptListOfItems() throws Exception {
        long userId = 1L;
        List<Item> itemList = List.of(
                TestObjectMaker.makeItem(1, null, true),
                TestObjectMaker.makeItem(2, null, true),
                TestObjectMaker.makeItem(3, null, true));

        when(itemService.getAllItemsOfUser(anyLong(), any())).thenReturn(itemList);

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(itemList)));
    }

    @Test
    void getItemsOfUser_expectUserNotFound() throws Exception {
        long userId = 1L;

        when(itemService.getAllItemsOfUser(anyLong(), any())).thenThrow(new UserNotFoundException(String.valueOf(userId)));

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", "0")
                        .param("size", "5"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createItem_ExpectCreatedNewItem() throws Exception {
        long userId = 1;
        ItemWithRequestDto itemWithRequestDto = TestObjectMaker.makeItemRequestDto(true);
        ItemCreateDto itemCreateDto = TestObjectMaker.makeItemDto(true);

        when(itemService.addItem(userId, itemCreateDto)).thenReturn(itemWithRequestDto);

        mvc.perform(post("/items")
                    .header("X-Sharer-User-Id", userId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(itemCreateDto)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(itemWithRequestDto)));
    }

    @Test
    void getItemById_expectSingleItem() throws Exception {
        long userId = 1L;
        User user = new User();
        user.setId(1L);
        Item item = TestObjectMaker.makeItem(1, user, true);

        when(itemService.getItemById(1, 1)).thenReturn(item);

        mvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(item)));
    }

    @Test
    void getItemById_expectItemNotFound() throws Exception {
        long userId = 1L;

        when(itemService.getItemById(1, 1)).thenThrow(new ItemNotFoundException(String.valueOf(2L)));

        mvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isNotFound());
    }

    @Test
    void getItemById_expectInternalServerError() throws Exception {
        long userId = 1L;

        mvc.perform(get("/items/wrongId")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void redactItemInfo_expectSuccess() throws Exception {
        long userId = 1L;
        User user = new User();
        user.setId(1L);
        Item item = TestObjectMaker.makeItem(1, user, true);
        ItemPatchDto itemPatchDto = new ItemPatchDto(1,"updatedName", "updatedDesc", true);

        when(itemService.redactItem(1, 1, itemPatchDto)).thenReturn(item);

        mvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemPatchDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(item)));
    }

    @Test
    void redactItemInfo_expectItemNotFound() throws Exception {
        long userId = 1L;
        ItemPatchDto itemPatchDto = new ItemPatchDto(1,"updatedName", "updatedDesc", true);

        when(itemService.redactItem(1, 1, itemPatchDto)).thenThrow(new ItemNotFoundException(""));

        mvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemPatchDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteItem() throws Exception {
        Item item = TestObjectMaker.makeItem(1, null, true);

        when(itemService.deleteItem(item.getId())).thenReturn(item);

        mvc.perform(delete("/items/" + item.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(item)));
    }

    @Test
    void deleteItem_expectItemNotFound() throws Exception {
        Item item = TestObjectMaker.makeItem(1, null, true);

        when(itemService.deleteItem(item.getId())).thenThrow(new ItemNotFoundException(""));

        mvc.perform(delete("/items/" + item.getId()))
                .andExpect(status().isNotFound());
    }


    @Test
    void searchItem_expectList() throws Exception {
        List<Item> itemList = List.of(
                TestObjectMaker.makeItem(1, null, true),
                TestObjectMaker.makeItem(2, null, true),
                TestObjectMaker.makeItem(3, null, true));

        when(itemService.searchItem("test", PageRequest.of(0, 5))).thenReturn(itemList);

        mvc.perform(get("/items/search")
                        .param("text", "test")
                        .param("from", String.valueOf(1))
                        .param("size", String.valueOf(5)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(itemList)));
    }

    @Test
    void addComment() throws Exception {
        long userId = 1;
        long itemId = 1;
        CommentPostDto commentPostDto = new CommentPostDto();
        commentPostDto.setText("test");

        CommentDto resultComment = new CommentDto();
        resultComment.setCreated(LocalDateTime.now());
        resultComment.setAuthorName("AuthorName");
        resultComment.setText("test");

        when(itemService.addComment(userId,itemId, commentPostDto)).thenReturn(resultComment);

        mvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentPostDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(resultComment)));
    }
}