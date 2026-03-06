package org.homework.consoleapp.model;

import org.homework.consoleapp.exception.ValidationException;

/**
 * Пользователь.
 *
 * @param name      Имя пользователя
 * @param email     Email пользователя
 * @param age       Возраст пользователя
 */
public record UserDtoIn(
    String name,
    String email,
    Integer age
) {
    public UserDtoIn {
        if (name == null || name.isBlank()) {
            throw new ValidationException("Имя пользователя не может быть пустым");
        }

        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new ValidationException("Некорректный формат email: " + email + ". Формат: [A-Za-z0-9+_.-]+@[A-Za-z0-9.-]");
        }

        if (age == null) {
            throw new ValidationException("Возраст не может быть null");
        }

        if (age < 0 || age > 120) {
            throw new ValidationException("Возраст должен быть в диапазоне от 0 до 120 лет. Получено: " + age);
        }
    }
}