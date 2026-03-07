package consoleapp.mapper;

import org.homework.consoleapp.mapper.UserMapper;
import org.homework.consoleapp.model.UserDtoIn;
import org.homework.infrastructure.entity.UserEntity;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTests {

    @Test
    void toDto_shouldMapAllFieldsFromEntityToDto() {
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

    @Test
    void toEntity_shouldMapAllFieldsFromDtoToEntity() {
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