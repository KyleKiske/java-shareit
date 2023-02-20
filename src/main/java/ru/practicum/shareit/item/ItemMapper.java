package ru.practicum.shareit.item;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public class ItemMapper {

    public Item dtoToItem(ItemDto itemDto) {
        if (itemDto == null) {
            return null;
        }
        Item item = new Item();

        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());

        return item;
    }

    public Item patchDtoToItem(ItemPatchDto itemPatchDto) {
        if (itemPatchDto == null) {
            return null;
        }

        Item item = new Item();

        item.setId(itemPatchDto.getId());
        item.setName(itemPatchDto.getName());
        item.setDescription(itemPatchDto.getDescription());
        item.setAvailable(itemPatchDto.getAvailable());

        return item;
    }
}
