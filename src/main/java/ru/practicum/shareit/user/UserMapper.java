package ru.practicum.shareit.user;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public class UserMapper {

    public User dtoToUser(UserDto userDto) {
        if (userDto == null) {
            return null;
        }
        User user = new User();

        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());

        return user;
    }
}
