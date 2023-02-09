package ru.yandex.practicum.user.repository;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.user.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository {

    User createUser(User user);

    User redactUser(long userId, User user);

    Optional<User> getUserById(long userId);

    Optional<User> getUserByEmail(String email);

    List<User> getAllUsers();

    void deleteUser(long userId);
}
