package org.homework.service;

import org.homework.entity.UserEntity;
import org.homework.exception.UserNotFoundException;
import org.homework.model.UserDtoIn;
import org.homework.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Nested
    @DisplayName("createUser. Создание пользователя")
    class CreateUserTests {

        @Test
        @DisplayName("Создаёт пользователя и возвращает DTO")
        void shouldCreateUserAndReturnDto() {
            // given
            var userDtoIn = new UserDtoIn("test", "test@example.com", 30);
            var userEntity = new UserEntity(1L, "test", "test@example.com", 30);

            given(userRepository.save(any(UserEntity.class))).willReturn(userEntity);

            // when
            var result = userService.createUser(userDtoIn);

            // then
            assertNotNull(result);
            assertEquals(1L, result.getId());
            assertEquals("test", result.getName());
            assertEquals("test@example.com", result.getEmail());
            assertEquals(30, result.getAge());

            verify(userRepository).save(any(UserEntity.class));
        }
    }

    @Nested
    @DisplayName("getById. Получение пользователя по ID")
    class GetByIdTests {

        @Test
        @DisplayName("Возвращает пользователя по существующему ID")
        void shouldReturnUserWhenExists() {
            // given
            var userEntity = new UserEntity(1L, "test", "test@example.com", 30);
            given(userRepository.findById(1L)).willReturn(Optional.of(userEntity));

            // when
            var result = userService.getById(1L);

            // then
            assertNotNull(result);
            assertEquals(1L, result.getId());
            assertEquals("test", result.getName());
        }

        @Test
        @DisplayName("Бросает UserNotFoundException, если пользователь не найден")
        void shouldThrowExceptionWhenUserNotFound() {
            // given
            given(userRepository.findById(999L)).willReturn(Optional.empty());

            // when & then
            var exception = assertThrows(UserNotFoundException.class, () -> userService.getById(999L));
            assertTrue(exception.getMessage().contains("999"));
        }
    }

    @Nested
    @DisplayName("getAllUsers. Получение всех пользователей")
    class GetAllUsersTests {

        @Test
        @DisplayName("Возвращает пустой список, когда пользователей нет")
        void shouldReturnEmptyListWhenNoUsers() {
            // given
            given(userRepository.findAll()).willReturn(List.of());

            // when
            var result = userService.getAllUsers();

            // then
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Возвращает список всех пользователей")
        void shouldReturnAllUsers() {
            // given
            var user1 = new UserEntity(1L, "test", "test@example.com", 30);
            var user2 = new UserEntity(2L, "Jane", "jane@example.com", 25);
            given(userRepository.findAll()).willReturn(List.of(user1, user2));

            // when
            var result = userService.getAllUsers();

            // then
            assertEquals(2, result.size());
            assertTrue(result.stream().anyMatch(u -> u.getId().equals(1L)));
            assertTrue(result.stream().anyMatch(u -> u.getId().equals(2L)));
        }
    }

    @Nested
    @DisplayName("updateUser. Обновление пользователя")
    class UpdateUserTests {

        @Test
        @DisplayName("Обновляет существующего пользователя")
        void shouldUpdateExistingUser() {
            // given
            var existingUser = new UserEntity(1L, "Old Name", "old@example.com", 20);
            var updateDto = new UserDtoIn("New Name", "new@example.com", 30);

            given(userRepository.findById(1L)).willReturn(Optional.of(existingUser));
            given(userRepository.save(any(UserEntity.class))).willAnswer(invocation -> invocation.getArgument(0));

            // when
            var result = userService.updateUser(1L, updateDto);

            // then
            assertEquals("New Name", result.getName());
            assertEquals("new@example.com", result.getEmail());
            assertEquals(30, result.getAge());

            verify(userRepository).save(existingUser);
        }

        @Test
        @DisplayName("Бросает UserNotFoundException, если пользователь не найден для обновления")
        void shouldThrowExceptionWhenUserNotFoundForUpdate() {
            // given
            var updateDto = new UserDtoIn("Name", "email@test.com", 25);
            given(userRepository.findById(999L)).willReturn(Optional.empty());

            // when & then
            assertThrows(UserNotFoundException.class, () -> userService.updateUser(999L, updateDto));
            verify(userRepository, never()).save(any(UserEntity.class));
        }
    }

    @Nested
    @DisplayName("deleteUser. Удаление пользователя")
    class DeleteUserTests {

        @Test
        @DisplayName("Удаляет существующего пользователя")
        void shouldDeleteExistingUser() {
            // given
            given(userRepository.existsById(1L)).willReturn(true);

            // when
            userService.deleteUser(1L);

            // then
            verify(userRepository).deleteById(1L);
        }

        @Test
        @DisplayName("Бросает UserNotFoundException, если пользователь не найден для удаления")
        void shouldThrowExceptionWhenUserNotFoundForDelete() {
            // given
            given(userRepository.existsById(999L)).willReturn(false);

            // when & then
            assertThrows(UserNotFoundException.class, () -> userService.deleteUser(999L));
            verify(userRepository, never()).deleteById(anyLong());
        }
    }
}