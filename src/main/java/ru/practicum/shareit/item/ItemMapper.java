package ru.practicum.shareit.item;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ItemMapper {

    Item dtoToItem(ItemDto itemDto);

    Item patchDtoToItem(ItemPatchDto itemPatchDto);
}