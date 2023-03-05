package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentPostDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemPatchDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@RestController()
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemClient itemClient;

    private static final String SHARER_USER_ID = "X-Sharer-User-Id";

    @GetMapping
    public ResponseEntity<Object> getItemsOfUser(
            @RequestHeader(SHARER_USER_ID) long userId,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return itemClient.getAllItemsOfUser(userId, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createItem(
            @RequestHeader(SHARER_USER_ID) long userId,
            @Valid @RequestBody ItemCreateDto itemCreateDto) {
        return itemClient.createItem(userId, itemCreateDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(
            @PathVariable long itemId,
            @RequestHeader(SHARER_USER_ID) long userId) {
        return itemClient.getItemById(userId, itemId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> redactItemInfo(
            @RequestHeader(SHARER_USER_ID) long userId,
            @PathVariable long itemId,
            @Valid @RequestBody ItemPatchDto itemPatchDto) {
        return itemClient.redactItem(userId, itemId, itemPatchDto);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@PathVariable long itemId) {
        itemClient.deleteItem(itemId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItem(@RequestHeader(SHARER_USER_ID) long userId,
                @RequestParam String text,
                @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return itemClient.searchItem(userId, text, from, size);
    }

    @PostMapping("{itemId}/comment")
    public ResponseEntity<Object> addComment(
                @RequestHeader(SHARER_USER_ID) long userId,
                @PathVariable long itemId,
                @Valid @RequestBody CommentPostDto commentPostDto) {
        return itemClient.addComment(userId, itemId, commentPostDto);
    }
}