package consoleapp.mapper;

import org.homework.consoleapp.mapper.UserMapper;
import org.homework.consoleapp.model.UserDtoIn;
import org.homework.infrastructure.entity.UserEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTests {

    @Nested
    @DisplayName("toDto. Конвертация UserEntity в UserDtoIn")
    class ToDtoTests {

        @Test
        @DisplayName("Вернуть null при передаче null")
        void shouldReturnNull_whenEntityIsNull() {
            // when
            var result = UserMapper.toDto(null);

            // then
            assertNull(result);
        }

        @Test
        @DisplayName("Успешно замаппить все поля из Entity в DTO")
        void shouldMapAllFieldsFromEntityToDto() {
            // given
            var id = 1L;
            var name = "test";
            var email = "test@example.com";
            var age = 25;
            var createdAt = LocalDateTime.of(2024, 1, 15, 10, 30);

            var entity = new UserEntity();
            entity.setId(id);
            entity.setName(name);
            entity.setEmail(email);
            entity.setAge(age);
            entity.setCreatedAt(createdAt);

            // when
            var result = UserMapper.toDto(entity);

            // then
            assertNotNull(result);
            assertEquals(id, result.id());
            assertEquals(name, result.name());
            assertEquals(email, result.email());
            assertEquals(age, result.age());
            assertEquals(createdAt, result.createdAt());
        }
    }

    @Nested
    @DisplayName("toEntity. Конвертация UserDtoIn в UserEntity")
    class ToEntityTests {

        @Test
        @DisplayName("Вернуть null при передаче null")
        void shouldReturnNull_whenDtoIsNull() {
            // when
            var result = UserMapper.toEntity(null);

            // then
            assertNull(result);
        }

        @Test
        @DisplayName("Успешно замаппить все поля из DTO в Entity")
        void shouldMapAllFieldsFromDtoToEntity() {
            // given
            var name = "test";
            var email = "test@example.com";
            var age = 30;

            var dto = new UserDtoIn(name, email, age);

            // when
            var result = UserMapper.toEntity(dto);

            // then
            assertNotNull(result);
            assertNull(result.getId());
            assertEquals(name, result.getName());
            assertEquals(email, result.getEmail());
            assertEquals(age, result.getAge());
            assertNull(result.getCreatedAt());
        }
    }
}