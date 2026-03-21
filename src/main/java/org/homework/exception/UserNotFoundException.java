package org.homework.exception;

public class UserNotFoundException extends BusinessLogicException {
    public UserNotFoundException(Long id) {
        super("Пользователь не найден", id.toString());
    }
}
