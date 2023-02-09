package ru.yandex.practicum.user.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.exception.EmptyEmailException;
import ru.yandex.practicum.exception.EmailAlreadyExistException;
import ru.yandex.practicum.user.User;

import java.util.*;

@Component
@Slf4j
public class InMemoryUserRepository implements UserRepository{
    public static Map<Long, User> userMap = new HashMap<>();
    private long currentId = 1;

    @Override
    public User createUser(User user) {
        if (user.getEmail() == null){
            throw new EmptyEmailException("email не указан.");
        }
        for (Map.Entry<Long, User> entry: userMap.entrySet()){
            if (entry.getValue().getEmail().equals(user.getEmail())){
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
        for (Map.Entry<Long, User> entry: userMap.entrySet()){
            if ((entry.getValue().getEmail().equalsIgnoreCase(user.getEmail())) && (entry.getKey() != userId)){
                throw new EmailAlreadyExistException(user.getEmail());
            }
        }
        userMap.replace(userId, user);
        return user;
    }

    @Override
    public Optional<User> getUserById(long userId){
        return Optional.ofNullable(userMap.get(userId));
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return userMap.values()
                .stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst();
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
