package org.homework.model;

import java.time.LocalDateTime;

/**
 * Пользователь.
 */
public class UserDtoOut {
    private final Long id;
    private final String name;
    private final String email;
    private final Integer age;
    private final LocalDateTime createdAt;

    /**
     * Конструктор.
     *
     * @param id        Идентификатор пользователя
     * @param name      Имя пользователя
     * @param email     Email пользователя
     * @param age       Возраст пользователя
     * @param createdAt Дата создания
     */
    public UserDtoOut(Long id, String name, String email, Integer age, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.age = age;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public Integer getAge() {
        return age;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
