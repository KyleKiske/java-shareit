package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.WrongUserException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemMapper itemMapper;
    private final ItemRepository itemRepository;
    private final UserService userService;

    public Item addItem(long userId, ItemDto itemDto) {
        User user = userService.getUserById(userId);
        Item item = itemMapper.dtoToItem(itemDto);
        item.setOwner(user.getId());
        return itemRepository.addItem(item);
    }

    public Item redactItem(long userId, long itemId, ItemPatchDto itemPatchDto) {
        Item itemFromRepo = itemRepository.getItemById(itemId);
        if (itemFromRepo.getOwner() != userId) {
            throw new WrongUserException("У вас нет доступа к данной вещи.");
        }
        Item item = itemMapper.patchDtoToItem(itemPatchDto);
        return itemRepository.redactItem(itemId, item);
    }

    public Item getItemById(long itemId) {
        return itemRepository.getItemById(itemId);
    }

    public List<Item> searchItem(String text) {
        if (text.isBlank()) {
            return List.of();
        }
        return itemRepository.searchItem(text);
    }

    public List<Item> getAllItemsOfUser(Long userId) {
        return itemRepository.getAllItemsOfUser(userId);
    }

    public void deleteItem(long itemId) {
        itemRepository.deleteItem(itemId);
    }
}
