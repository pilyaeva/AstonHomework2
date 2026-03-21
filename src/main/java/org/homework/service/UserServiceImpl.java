package org.homework.service;

import org.homework.exception.UserNotFoundException;
import org.homework.mapper.UserMapper;
import org.homework.model.UserDtoIn;
import org.homework.model.UserDtoOut;
import org.homework.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public UserDtoOut createUser(UserDtoIn userDto) {
        var userEntity = UserMapper.toEntity(userDto);
        var createdUser = userRepository.save(userEntity);
        logger.info("Пользователь создан: {}", createdUser.getId());
        return UserMapper.toDto(createdUser);
    }

    @Override
    public UserDtoOut getById(Long id) {
        var existingUser = userRepository.findById(id);

        if (existingUser.isEmpty()) {
            throw new UserNotFoundException(id);
        }

        return UserMapper.toDto(existingUser.get());
    }

    @Override
    public List<UserDtoOut> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public UserDtoOut updateUser(Long id, UserDtoIn userDto) {
        var existingUser = userRepository.findById(id);

        if (existingUser.isEmpty()) {
            throw new UserNotFoundException(id);
        }

        existingUser.get().setName(userDto.name());
        existingUser.get().setEmail(userDto.email());
        existingUser.get().setAge(userDto.age());

        var updated = userRepository.save(existingUser.get());
        logger.info("Пользователь обновлён: {}", id);
        return UserMapper.toDto(updated);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(id);
        }
        userRepository.deleteById(id);
        logger.info("Пользователь удалён: {}", id);
    }
}
