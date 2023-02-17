package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.Item;

import java.util.List;

@Repository
public interface ItemRepository {

    Item addItem(Item item);

    Item redactItem(long id, Item item);

    Item getItemById(long id);

    List<Item> searchItem(String text);

    List<Item> getAllItemsOfUser(long userId);

    void deleteItem(long itemId);
}
