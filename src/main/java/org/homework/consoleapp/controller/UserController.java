package org.homework.consoleapp.controller;

import org.homework.consoleapp.mapper.UserMapper;
import org.homework.consoleapp.model.UserDtoIn;
import org.homework.consoleapp.model.UserDtoOut;
import org.homework.infrastructure.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void createUser(UserDtoIn userDto) {
        var userEntity = UserMapper.toEntity(userDto);
        userRepository.create(userEntity);
        logger.info("Пользователь создан: {}", userDto.name());
    }

    public List<UserDtoOut> getAllUsers() {
        return userRepository.findAll().stream()
            .map(UserMapper::toDto)
            .toList();
    }

    public boolean updateUser(Long id, UserDtoIn userDto) {
        var userOpt = userRepository.findById(id);

        if (userOpt.isEmpty()) {
            logger.warn("Пользователь не найден: {}", id);
            return false;
        }

        var existingUser = userOpt.get();

        existingUser.setName(userDto.name());
        existingUser.setEmail(userDto.email());
        existingUser.setAge(userDto.age());

        userRepository.update(existingUser);
        logger.info("Пользователь обновлён: {}", id);
        return true;
    }

    public boolean deleteUser(Long id) {
        if (!userRepository.findById(id).isPresent()) {
            logger.warn("Пользователь не найден: {}", id);
            return false;
        }

        userRepository.delete(id);
        logger.info("Пользователь удалён: {}", id);
        return true;
    }
}