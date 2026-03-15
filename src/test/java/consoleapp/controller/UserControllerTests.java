package consoleapp.controller;

import org.homework.consoleapp.controller.UserController;
import org.homework.infrastructure.entity.UserEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.homework.consoleapp.mapper.UserMapper;
import org.homework.consoleapp.model.UserDtoIn;
import org.homework.consoleapp.model.UserDtoOut;
import org.homework.infrastructure.repository.UserRepository;
import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.Select;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, InstancioExtension.class})
class UserControllerTests {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserController userController;

    @Nested
    @DisplayName("createUser. Создание пользователя")
    class CreateUserTests {

        @Test
        @DisplayName("Должен вызвать маппер, репозиторий и залогировать информацию")
        void shouldCallMapperAndRepositoryAndLogInfo() {
            // given
            var id = Instancio.create(Long.class);
            var userDto = new UserDtoIn("Updated Name", "updated@example.com", 30);
            var userEntity = Instancio.of(UserEntity.class)
                    .set(Select.field(UserEntity::getId), id)
                    .create();

            // when
            try (var mapperMock = mockStatic(UserMapper.class)) {
                mapperMock.when(() -> UserMapper.toEntity(userDto)).thenReturn(userEntity);

                userController.createUser(userDto);

                // then
                mapperMock.verify(() -> UserMapper.toEntity(userDto));
                verify(userRepository).create(userEntity);
            }
        }
    }

    @Nested
    @DisplayName("getAllUsers. Получение всех пользователей")
    class GetAllUsersTests {

        @Test
        @DisplayName("Вернуть пустой список, когда репозиторий возвращает пустой список")
        void shouldReturnEmptyListWhenRepositoryReturnsEmpty() {
            // given
            when(userRepository.findAll()).thenReturn(List.of());

            // when
            var result = userController.getAllUsers();

            // then
            assertNotNull(result);
            assertTrue(result.isEmpty());
            verify(userRepository).findAll();
        }

        @Test
        @DisplayName("Сконвертировать сущности в DTO через маппер, когда пользователи есть")
        void shouldMapEntitiesToDtosWhenUsersExist() {
            // given
            var id = Instancio.create(Long.class);
            var userEntity = Instancio.of(UserEntity.class)
                    .set(Select.field(UserEntity::getId), id)
                    .create();
            var userDto = Instancio.of(UserDtoOut.class)
                    .set(Select.field(UserDtoOut::id), userEntity.getId())
                    .create();

            when(userRepository.findAll()).thenReturn(List.of(userEntity));

            // when
            try (var mapperMock = mockStatic(UserMapper.class)) {
                mapperMock.when(() -> UserMapper.toDto(userEntity)).thenReturn(userDto);

                var result = userController.getAllUsers();

                // then
                mapperMock.verify(() -> UserMapper.toDto(userEntity));
                assertEquals(1, result.size());
                assertEquals(userDto, result.get(0));
            }
        }

        @Test
        @DisplayName("Вызвать маппер для каждой сущности при множественных пользователях")
        void shouldCallMapperForEachEntity() {
            // given
            var userCount = 2;
            var idGenerator = new AtomicLong(1L);
            var entities = Instancio.ofList(UserEntity.class)
                    .size(userCount)
                    .supply(Select.field(UserEntity::getId), idGenerator::getAndIncrement)
                    .create();

            var dtos = entities.stream()
                    .map(e -> Instancio.of(UserDtoOut.class)
                            .set(Select.field(UserDtoOut::id), e.getId())
                            .set(Select.field(UserDtoOut::name), e.getName())
                            .set(Select.field(UserDtoOut::email), e.getEmail())
                            .set(Select.field(UserDtoOut::age), e.getAge())
                            .create())
                    .toList();

            when(userRepository.findAll()).thenReturn(entities);

            // when
            try (var mapperMock = mockStatic(UserMapper.class)) {
                for (int i = 0; i < entities.size(); i++) {
                    final var entity = entities.get(i);
                    final var dto = dtos.get(i);

                    mapperMock.when(() -> UserMapper.toDto(entity)).thenReturn(dto);
                }

                var result = userController.getAllUsers();

                // then
                assertEquals(userCount, result.size());
                assertTrue(result.containsAll(dtos));
            }
        }
    }

    @Nested
    @DisplayName("updateUser. Обновление пользователя")
    class UpdateUserTests {

        @Test
        @DisplayName("Успешно обновить существующего пользователя и вернуть true")
        void shouldUpdateExistingUserAndReturnTrue() {
            // given
            var id = Instancio.create(Long.class);
            var existingUser = Instancio.of(UserEntity.class)
                    .set(Select.field(UserEntity::getId), id)
                    .create();
            var updateDto = new UserDtoIn("Updated Name", "updated@example.com", 30);

            when(userRepository.findById(id)).thenReturn(Optional.of(existingUser));

            // when
            var result = userController.updateUser(id, updateDto);

            // then
            assertTrue(result);
            assertEquals(updateDto.name(), existingUser.getName());
            assertEquals(updateDto.email(), existingUser.getEmail());
            assertEquals(updateDto.age(), existingUser.getAge());
            verify(userRepository).update(existingUser);
        }

        @Test
        @DisplayName("Вернуть false и залогировать предупреждение, когда пользователь не найден")
        void shouldReturnFalseAndLogWarnWhenUserNotFound() {
            // given
            var id = Instancio.create(Long.class);
            UserDtoIn userDto = null;

            when(userRepository.findById(id)).thenReturn(Optional.empty());

            // when
            var result = userController.updateUser(id, userDto);

            // then
            assertFalse(result);
            verify(userRepository, never()).update(any());
        }
    }

    @Nested
    @DisplayName("deleteUser. Удаление пользователя")
    class DeleteUserTests {

        @Test
        @DisplayName("Удалить существующего пользователя и вернуть true")
        void shouldDeleteExistingUserAndReturnTrue() {
            // given
            var id = Instancio.create(Long.class);
            var userEntity = Instancio.of(UserEntity.class)
                    .set(Select.field(UserEntity::getId), id)
                    .create();

            when(userRepository.findById(id)).thenReturn(Optional.of(userEntity));

            // when
            var result = userController.deleteUser(id);

            // then
            assertTrue(result);
            verify(userRepository).delete(id);
        }

        @Test
        @DisplayName("Вернуть false и залогировать предупреждение, когда пользователь не найден")
        void shouldReturnFalseAndLogWarnWhenUserNotFound() {
            // given
            var id = Instancio.create(Long.class);

            when(userRepository.findById(id)).thenReturn(Optional.empty());

            // when
            var result = userController.deleteUser(id);

            // then
            assertFalse(result);
            verify(userRepository, never()).delete(id);
        }
    }
}