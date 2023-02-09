package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public List<Item> getItemsOfUser(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.getAllItemsOfUser(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Item createItem(@RequestHeader(name = "X-Sharer-User-Id") long userId,
                           @Valid @RequestBody ItemDto itemDto) {
        return itemService.addItem(userId, itemDto);
    }

    @GetMapping("/{itemId}")
    public Item getItemById(@PathVariable long itemId) {
        return itemService.getItemById(itemId);
    }

    @PatchMapping("/{itemId}")
    public Item redactItemInfo(@RequestHeader("X-Sharer-User-Id") long userId,
                                @PathVariable long itemId,
                                @Valid @RequestBody ItemPatchDto itemPatchDto) {
        return itemService.redactItem(userId, itemId, itemPatchDto);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@PathVariable long itemId) {
        itemService.deleteItem(itemId);
    }

    @GetMapping("/search")
    public List<Item> searchItem(@RequestParam String text) {
        return itemService.searchItem(text);
    }
}