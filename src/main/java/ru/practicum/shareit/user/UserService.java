package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EmailAlreadyExistException;
import ru.practicum.shareit.exception.UserNotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public User createUser(UserDto userDto) {
        User user = userMapper.dtoToUser(userDto);
        return userRepository.save(user);
    }

    public User redactUser(Long userId, UserPatchDto userPatchDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId.toString()));

        if (userPatchDto.getEmail() != null) {
            emailDuplicateCheck(userPatchDto.getEmail());
            user.setEmail(userPatchDto.getEmail());
        }
        if (userPatchDto.getName() != null) {
            user.setName(userPatchDto.getName());
        }
        return userRepository.save(user);
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId.toString()));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void deleteUser(Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId.toString()));
        userRepository.deleteById(userId);
    }

    private void emailDuplicateCheck(String email) {
        userRepository.findByEmail(email).ifPresent(user -> {
            throw new EmailAlreadyExistException(email);
        });
    }
}
