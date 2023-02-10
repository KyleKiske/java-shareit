package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public User createUser(UserDto userDto) {
        User user = userMapper.dtoToUser(userDto);
        return userRepository.createUser(user);
    }

    public User redactUser(Long userId, UserPatchDto userPatchDto) {
        User user = userRepository.getUserById(userId);

        if (userPatchDto.getEmail() != null) {
            emailDuplicateCheck(userPatchDto.getEmail());
            user.setEmail(userPatchDto.getEmail());
        }
        if (userPatchDto.getName() != null) {
            user.setName(userPatchDto.getName());
        }
        return userRepository.redactUser(userId, user);
    }

    public User getUserById(Long userId) {
        return userRepository.getUserById(userId);
    }

    public List<User> getAllUsers() {
        return userRepository.getAllUsers();
    }

    public void deleteUser(Long userId) {
        userRepository.deleteUser(userId);
    }

    private void emailDuplicateCheck(String email) {
        userRepository.checkUserByEmail(email);
    }

}
