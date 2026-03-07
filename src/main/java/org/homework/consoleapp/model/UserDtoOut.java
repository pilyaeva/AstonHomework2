package org.homework.consoleapp.model;

import java.time.LocalDateTime;

/**
 * Пользователь.
 *
 * @param id        Идентификатор пользователя
 * @param name      Имя пользователя
 * @param email     Email пользователя
 * @param age       Возраст пользователя
 * @param createdAt Дата создания
 */
public record UserDtoOut(
    Long id,
    String name,
    String email,
    Integer age,
    LocalDateTime createdAt
) {
}
