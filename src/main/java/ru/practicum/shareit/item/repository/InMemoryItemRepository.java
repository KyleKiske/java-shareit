package ru.practicum.shareit.item.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.item.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class InMemoryItemRepository implements ItemRepository {
    public Map<Long, Item> itemMap = new HashMap<>();
    private long currentId = 1;

    @Override
    public Item addItem(Item item) {
        item.setId(currentId);
        itemMap.put(currentId, item);
        currentId++;
        return item;
    }

    @Override
    public Item redactItem(long id, Item item) {
        Item item1 = itemMap.get(id);
        if (item.getAvailable() != null) {
            item1.setAvailable(item.getAvailable());
        }
        if (item.getName() != null) {
            item1.setName(item.getName());
        }
        if (item.getDescription() != null) {
            item1.setDescription(item.getDescription());
        }
        return itemMap.replace(id, item1);
    }

    @Override
    public Item getItemById(long id) {
        Item item = itemMap.get(id);
        if (item == null) {
            throw new ItemNotFoundException("");
        }
        return item;
    }

    @Override
    public List<Item> searchItem(String text) {
        return itemMap.values().stream()
                .filter(item -> (item.getName().toLowerCase().contains(text.toLowerCase())
                    || item.getDescription().toLowerCase().contains(text.toLowerCase()))
                    && item.getAvailable())
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> getAllItemsOfUser(long userId) {
        return itemMap.values().stream()
                .filter(item -> item.getOwner() == userId)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteItem(long itemId) {
        itemMap.remove(itemId);
    }

}
