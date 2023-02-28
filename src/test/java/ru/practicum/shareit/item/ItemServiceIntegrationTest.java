package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.TestObjectMaker;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceIntegrationTest {
    private final ItemService itemService;
    private final UserService userService;

    @Test
    public void getAllItemsOfUser_expectListOfItems() {
        User user = userService.createUser(new UserDto("User One", "test@email.com"));
        User userTwo = userService.createUser(new UserDto("User Two", "test@email2.com"));

        ItemCreateDto itemCreateDto = TestObjectMaker.makeItemDto(true);

        itemService.addItem(user.getId(), itemCreateDto);
        itemService.addItem(user.getId(), itemCreateDto);
        itemService.addItem(user.getId(), itemCreateDto);

        itemService.addItem(userTwo.getId(), itemCreateDto);

        List<Item> items = itemService.getAllItemsOfUser(user.getId(), PageRequest.of(0, 5));
        List<Item> itemsUserTwo = itemService.getAllItemsOfUser(userTwo.getId(), PageRequest.of(0, 5));

        assertEquals(items.size(), 3);
        assertEquals(itemsUserTwo.size(), 1);

    }
}