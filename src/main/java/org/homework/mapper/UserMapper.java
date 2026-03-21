package org.homework.mapper;

import org.homework.model.UserDtoIn;
import org.homework.model.UserDtoOut;
import org.homework.entity.UserEntity;

public class UserMapper {
    public static UserDtoOut toDto(UserEntity entity) {
        if (entity == null) return null;
        return new UserDtoOut(
            entity.getId(),
            entity.getName(),
            entity.getEmail(),
            entity.getAge(),
            entity.getCreatedAt()
        );
    }

    public static UserEntity toEntity(UserDtoIn dto) {
        if (dto == null) return null;
        UserEntity entity = new UserEntity();
        entity.setName(dto.name());
        entity.setEmail(dto.email());
        entity.setAge(dto.age());
        return entity;
    }
}
