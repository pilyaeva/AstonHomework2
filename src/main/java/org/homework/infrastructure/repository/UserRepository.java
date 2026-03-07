package org.homework.infrastructure.repository;

import org.homework.infrastructure.entity.UserEntity;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    void create(UserEntity user);
    Optional<UserEntity> findById(Long id);
    List<UserEntity> findAll();
    void update(UserEntity user);
    void delete(Long id);
}
