package consoleapp.controller;

import org.homework.consoleapp.controller.UserController;
import org.homework.infrastructure.entity.UserEntity;
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
import org.mockito.MockedStatic;
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

    @Test
    void createUser_shouldCallMapperAndRepositoryAndLogInfo() {
        // given
        var userDto = new UserDtoIn("Updated Name", "updated@example.com", 30);
        var userEntity = Instancio.of(UserEntity.class)
                .set(Select.field(UserEntity::getId), 1L)
                .create();

        // when
        try (MockedStatic<UserMapper> mapperMock = mockStatic(UserMapper.class)) {
            mapperMock.when(() -> UserMapper.toEntity(userDto)).thenReturn(userEntity);

            userController.createUser(userDto);

            // then
            mapperMock.verify(() -> UserMapper.toEntity(userDto));
            verify(userRepository).create(userEntity);
        }
    }

    @Test
    void getAllUsers_shouldReturnEmptyListWhenRepositoryReturnsEmpty() {
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
    void getAllUsers_shouldMapEntitiesToDtosWhenUsersExist() {
        // given
        var userEntity = Instancio.of(UserEntity.class)
                .set(Select.field(UserEntity::getId), 1L)
                .create();
        var userDto = Instancio.of(UserDtoOut.class)
                .set(Select.field(UserDtoOut::id), userEntity.getId())
                .create();

        when(userRepository.findAll()).thenReturn(List.of(userEntity));

        // when
        try (MockedStatic<UserMapper> mapperMock = mockStatic(UserMapper.class)) {
            mapperMock.when(() -> UserMapper.toDto(userEntity)).thenReturn(userDto);

            var result = userController.getAllUsers();

            // then
            mapperMock.verify(() -> UserMapper.toDto(userEntity));
            assertEquals(1, result.size());
            assertEquals(userDto, result.get(0));
        }
    }

    @Test
    void updateUser_shouldUpdateExistingUserAndReturnTrue() {
        // given
        var id = 1L;
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
        verify(userRepository).findById(id);
    }

    @Test
    void updateUser_shouldReturnFalseAndLogWarnWhenUserNotFound() {
        // given
        var id = Instancio.create(Long.class);
        var userDto = Instancio.create(UserDtoIn.class);

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        // when
        var result = userController.updateUser(id, userDto);

        // then
        assertFalse(result);
        verify(userRepository).findById(id);
        verify(userRepository, never()).update(any());
    }

    @Test
    void updateUser_shouldNotModifyRepositoryWhenUserNotFound() {
        // given
        var id = Instancio.create(Long.class);
        var userDto = Instancio.create(UserDtoIn.class);

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        // when
        var result = userController.updateUser(id, userDto);

        // then
        assertFalse(result);
        verify(userRepository, never()).update(any());
    }

    @Test
    void deleteUser_shouldDeleteExistingUserAndReturnTrue() {
        // given
        var id = 2L;
        var userEntity = Instancio.of(UserEntity.class)
                .set(Select.field(UserEntity::getId), id)
                .create();

        when(userRepository.findById(id)).thenReturn(Optional.of(userEntity));

        // when
        var result = userController.deleteUser(id);

        // then
        assertTrue(result);
        verify(userRepository).findById(id);
        verify(userRepository).delete(id);
    }

    @Test
    void deleteUser_shouldReturnFalseAndLogWarnWhenUserNotFound() {
        // given
        var id = Instancio.create(Long.class);

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        // when
        var result = userController.deleteUser(id);

        // then
        assertFalse(result);
        verify(userRepository).findById(id);
        verify(userRepository, never()).delete(id);
    }

    @Test
    void deleteUser_shouldNotCallDeleteWhenUserDoesNotExist() {
        // given
        var id = Instancio.create(Long.class);

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        // when
        var result = userController.deleteUser(id);

        // then
        assertFalse(result);
        verify(userRepository, never()).delete(anyLong());
    }

    @Test
    void getAllUsers_shouldCallMapperForEachEntity() {
        // given
        var idGenerator = new AtomicLong(1L);
        var entities = Instancio.ofList(UserEntity.class)
                .size(2)
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
        try (MockedStatic<UserMapper> mapperMock = mockStatic(UserMapper.class)) {
            for (int i = 0; i < entities.size(); i++) {
                final var entity = entities.get(i);
                final var dto = dtos.get(i);

                mapperMock.when(() -> UserMapper.toDto(entity)).thenReturn(dto);
            }

            var result = userController.getAllUsers();

            // then
            assertEquals(2, result.size());
            assertTrue(result.containsAll(dtos));
            for (var entity : entities) {
                mapperMock.verify(() -> UserMapper.toDto(entity));
            }
        }
    }

    @Test
    void createUser_shouldHandleNullFieldsInDto() {
        // given
        var userDto = new UserDtoIn("Updated Name", "updated@example.com", 30);

        var userEntity = Instancio.of(UserEntity.class)
                .set(Select.field(UserEntity::getName), null)
                .set(Select.field(UserEntity::getEmail), null)
                .set(Select.field(UserEntity::getAge), null)
                .create();

        // when
        try (MockedStatic<UserMapper> mapperMock = mockStatic(UserMapper.class)) {
            mapperMock.when(() -> UserMapper.toEntity(userDto)).thenReturn(userEntity);

            userController.createUser(userDto);

            // then
            mapperMock.verify(() -> UserMapper.toEntity(userDto));
            verify(userRepository).create(userEntity);
        }
    }
}