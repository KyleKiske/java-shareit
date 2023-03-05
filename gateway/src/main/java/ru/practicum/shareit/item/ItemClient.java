package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentPostDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemPatchDto;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {

    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> getAllItemsOfUser(long userId, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get("", userId, parameters);
    }

    public ResponseEntity<Object> createItem(long userId, ItemCreateDto itemCreateDto) {
        return post("", userId, itemCreateDto);
    }

    public ResponseEntity<Object> getItemById(long userId, Long itemId) {
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> redactItem(long userId, long itemId, ItemPatchDto itemPatchDto) {
        return patch("/" + itemId, userId, itemPatchDto);
    }

    public void deleteItem(long itemId) {
        delete("/" + itemId);
    }

    public ResponseEntity<Object> searchItem(long userId, String text, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "text", text,
                "from", from,
                "size", size
        );
        return get("/search?text={text}&from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> addComment(long userId, long itemId, CommentPostDto commentPost) {
        return post("/" + itemId + "/comment", userId, commentPost);
    }
}
