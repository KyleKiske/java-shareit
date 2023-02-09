package ru.yandex.practicum.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.exception.ItemNotFoundException;
import ru.yandex.practicum.exception.WrongUserException;
import ru.yandex.practicum.item.repository.ItemRepository;
import ru.yandex.practicum.user.User;
import ru.yandex.practicum.user.UserService;

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

    Item redactItem(long userId, long itemId, ItemPatchDto itemPatchDto) {
        Item itemFromRepo = itemRepository.getItemById(itemId).orElseThrow(() -> new ItemNotFoundException("Вещь не найдена"));
        if (itemFromRepo.getOwner() != userId){
            throw new WrongUserException("У вас нет доступа к данной вещи.");
        }
        Item item = itemMapper.patchDtoToItem(itemPatchDto);
        return itemRepository.redactItem(itemId, item);
    }

    Item getItemById(long itemId){
        return itemRepository.getItemById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Вещь с данным id не найдена"));
    }

    List<Item> searchItem(String text){
        if (text.isBlank()){
            return List.of();
        }
        return itemRepository.searchItem(text);
    }

    List<Item> getAllItemsOfUser(Long userId) {
        return itemRepository.getAllItemsOfUser(userId);
    }

    public void deleteItem(long itemId) {
        itemRepository.deleteItem(itemId);
    }
}
