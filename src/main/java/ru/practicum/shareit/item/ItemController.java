package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.PaginationMaker;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public List<Item> getItemsOfUser(@RequestHeader("X-Sharer-User-Id") long userId,
                                     @RequestParam(required = false) Integer from,
                                     @RequestParam(required = false) Integer size) {
        return itemService.getAllItemsOfUser(userId, PaginationMaker.makePageRequest(from, size)).toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemWithRequestDto createItem(@RequestHeader(name = "X-Sharer-User-Id") long userId,
                           @Valid @RequestBody ItemCreateDto itemCreateDto) {
        return itemService.addItem(userId, itemCreateDto);
    }

    @GetMapping("/{itemId}")
    public Item getItemById(@PathVariable long itemId,
                            @RequestHeader(name = "X-Sharer-User-Id") long userId) {
        return itemService.getItemById(itemId, userId);
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
    public List<Item> searchItem(@RequestParam String text,
                                 @RequestParam(required = false) Integer from,
                                 @RequestParam(required = false) Integer size) {
        return itemService.searchItem(text, PaginationMaker.makePageRequest(from, size)).toList();
    }

    @PostMapping("{itemId}/comment")
    public CommentDto addComment(@RequestHeader(name = "X-Sharer-User-Id") long userId,
                                 @PathVariable long itemId,
                                 @Valid @RequestBody CommentPostDto comment) {
        return itemService.addComment(userId, itemId, comment);
    }
}