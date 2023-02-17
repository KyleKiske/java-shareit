package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.User;

import java.util.List;

@Repository
public interface UserRepository {

    User createUser(User user);

    User redactUser(long userId, User user);

    User getUserById(long userId);

    void checkUserByEmail(String email);

    List<User> getAllUsers();

    void deleteUser(long userId);
}
