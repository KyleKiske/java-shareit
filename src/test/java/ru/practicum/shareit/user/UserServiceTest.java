package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.TestObjectMaker;
import ru.practicum.shareit.exception.EmailAlreadyExistException;
import ru.practicum.shareit.exception.UserNotFoundException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void redactUser_expectRedactedUser() {
        long id = 1;
        User user = TestObjectMaker.makeUser(id);
        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        assertEquals(userService.getUserById(id), user);
    }

    @Test
    void redactUser_expectUpdatedEmail() {
        long id = 1;
        User user = TestObjectMaker.makeUser(id);
        User updatedUser = TestObjectMaker.makeUser(id);
        String updateEmail = "updated@email.com";
        updatedUser.setEmail(updateEmail);
        UserPatchDto userPatchDto = new UserPatchDto(null, updateEmail);

        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        assertThat(userService.redactUser(id, userPatchDto)).isEqualTo(updatedUser);
    }

    @Test
    void redactUser_expectUpdatedName() {
        long id = 1;
        User user = TestObjectMaker.makeUser(id);
        User updatedUser = TestObjectMaker.makeUser(id);
        String updateName = "newName";
        updatedUser.setName(updateName);
        UserPatchDto userPatchDto = new UserPatchDto(updateName, null);

        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        assertThat(userService.redactUser(id, userPatchDto)).isEqualTo(updatedUser);
    }

    @Test
    void redactUser_expectUserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.redactUser(1L, new UserPatchDto()));
    }

    @Test
    void redactUser_expectEmailAlreadyExist() {
        long id = 1;
        User user = TestObjectMaker.makeUser(id);
        String updateEmail = "updated@email.com";
        UserPatchDto userPatchDto = new UserPatchDto("newName", updateEmail);

        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail(updateEmail)).thenReturn(Optional.of(user));

        assertThrows(EmailAlreadyExistException.class, () -> userService.redactUser(id, userPatchDto));
    }

    @Test
    void getUserById_expectReturnUser() {
        long id = 1;
        User user = TestObjectMaker.makeUser(id);
        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        assertEquals(userService.getUserById(id), user);
    }

    @Test
    void getUserById_expectUserNotFound() {
        long id = 0;
        when(userRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.getUserById(id));
    }

    @Test
    void getAllUsers_expectListOfUsers() {
        List<User> users = List.of(
                TestObjectMaker.makeUser(1),
                TestObjectMaker.makeUser(2),
                TestObjectMaker.makeUser(3));
        when(userRepository.findAll()).thenReturn(users);
        assertEquals(userService.getAllUsers(), users);
    }

    @Test
    void deleteUser_expectDeletedUser() {
        long userId = 1;
        User user = TestObjectMaker.makeUser(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.deleteUser(userId);
        verify(userRepository).deleteById(userId);
    }
}