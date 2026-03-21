package org.homework.service;

import org.homework.model.UserDtoIn;
import org.homework.model.UserDtoOut;

import java.util.List;

public interface UserService {
    UserDtoOut createUser(UserDtoIn userDto);
    UserDtoOut getById(Long id);
    List<UserDtoOut> getAllUsers();
    UserDtoOut updateUser(Long id, UserDtoIn userDto);
    void deleteUser(Long id);
}
