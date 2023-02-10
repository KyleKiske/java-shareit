package ru.practicum.shareit.user.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.EmptyEmailException;
import ru.practicum.shareit.exception.EmailAlreadyExistException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.User;

import java.util.*;

@Component
@Slf4j
public class InMemoryUserRepository implements UserRepository {
    public final Map<Long, User> userMap = new HashMap<>();
    private long currentId = 1;

    @Override
    public User createUser(User user) {
        if (user.getEmail() == null) {
            throw new EmptyEmailException("email не указан.");
        }
        for (Map.Entry<Long, User> entry: userMap.entrySet()) {
            if (entry.getValue().getEmail().equals(user.getEmail())) {
                throw new EmailAlreadyExistException(user.getEmail());
            }
        }
        user.setId(currentId);
        userMap.put(currentId, user);
        currentId++;
        return user;
    }

    @Override
    public User redactUser(long userId, User user) {
        for (Map.Entry<Long, User> entry: userMap.entrySet()) {
            if ((entry.getValue().getEmail().equalsIgnoreCase(user.getEmail())) && (entry.getKey() != userId)) {
                throw new EmailAlreadyExistException(user.getEmail());
            }
        }
        userMap.replace(userId, user);
        return user;
    }

    @Override
    public User getUserById(long userId) {
        User user = userMap.get(userId);
        if (user == null){
            throw new UserNotFoundException(String.valueOf(userId));
        }
        return user;
    }

    @Override
    public void checkUserByEmail(String email) {
        Optional<User> users = userMap.values()
                .stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst();
        if (users.isPresent()){
            throw new EmailAlreadyExistException(email);
        }
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(userMap.values());
    }

    @Override
    public void deleteUser(long userId) {
        userMap.remove(userId);
    }
}
