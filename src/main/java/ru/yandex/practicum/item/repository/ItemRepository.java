package ru.yandex.practicum.item.repository;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.item.Item;
import ru.yandex.practicum.item.ItemDto;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository {

    Item addItem (Item item);

    Item redactItem(long id, Item item);

    Optional<Item> getItemById(long id);

    List<Item> searchItem(String text);

    List<Item> getAllItemsOfUser(long userId);

    void deleteItem(long itemId);
}
