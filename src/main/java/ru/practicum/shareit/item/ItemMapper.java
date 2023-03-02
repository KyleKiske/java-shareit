package ru.practicum.shareit.item;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public class ItemMapper {

    public Item dtoToItem(ItemCreateDto itemCreateDto) {
        if (itemCreateDto == null) {
            return null;
        }
        Item item = new Item();

        item.setName(itemCreateDto.getName());
        item.setDescription(itemCreateDto.getDescription());
        item.setAvailable(itemCreateDto.getAvailable());

        return item;
    }

    public ItemWithRequestDto itemToRequestDto(Item item) {
        if (item == null) {
            return null;
        }
        ItemWithRequestDto itemWithRequestDto = new ItemWithRequestDto();
        itemWithRequestDto.setId(item.getId());
        itemWithRequestDto.setOwner(item.getOwner());
        itemWithRequestDto.setName(item.getName());
        itemWithRequestDto.setDescription(item.getDescription());
        itemWithRequestDto.setAvailable(item.getAvailable());
        if (item.getRequest() != null) {
            itemWithRequestDto.setRequestId(item.getRequest().getId());
        } else {
            itemWithRequestDto.setRequestId(null);
        }
        itemWithRequestDto.setLastBooking(item.getLastBooking());
        itemWithRequestDto.setNextBooking(item.getNextBooking());
        itemWithRequestDto.setComments(item.getComments());

        return itemWithRequestDto;
    }

}
