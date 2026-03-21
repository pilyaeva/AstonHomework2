package org.homework.model;

import org.homework.exception.ValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserDtoInTests {

    @Nested
    @DisplayName("constructor. Конструктор UserDtoIn")
    class Constructor {

        @Test
        @DisplayName("Успешное создание пользователя с корректными данными")
        void shouldCreateValidUserWithCorrectData() {
            // given
            var name = "test";
            var email = "test@example.com";
            var age = 25;

            // when
            var user = new UserDtoIn(name, email, age);

            // then
            assertEquals(name, user.name());
            assertEquals(email, user.email());
            assertEquals(age, user.age());
        }

        @Test
        @DisplayName("Имя пользователя null")
        void shouldThrowWhenNameIsNull() {
            // given
            String name = null;
            var email = "test@example.com";
            var age = 30;

            // when
            var exception = assertThrows(ValidationException.class, () -> new UserDtoIn(name, email, age));

            // then
            assertEquals("Имя пользователя не может быть пустым", exception.getMessage());
        }

        @Test
        @DisplayName("Имя пользователя пробелы")
        void shouldThrowWhenNameIsBlank() {
            // given
            var name = "   ";
            var email = "test@example.com";
            var age = 30;

            // when
            var exception = assertThrows(ValidationException.class, () -> new UserDtoIn(name, email, age));

            // then
            assertEquals("Имя пользователя не может быть пустым", exception.getMessage());
        }

        @Test
        @DisplayName("Имя пользователя пустое")
        void shouldThrowWhenNameIsEmpty() {
            // given
            var name = "";
            var email = "test@example.com";
            var age = 30;

            // when
            var exception = assertThrows(ValidationException.class, () -> new UserDtoIn(name, email, age));

            // then
            assertEquals("Имя пользователя не может быть пустым", exception.getMessage());
        }

        @Test
        @DisplayName("Email пользователя null")
        void shouldThrowWhenEmailIsNull() {
            // given
            var name = "test";
            String email = null;
            var age = 40;

            // when
            var exception = assertThrows(ValidationException.class, () -> new UserDtoIn(name, email, age));

            // then
            assertTrue(exception.getMessage().contains("Некорректный формат email"));
            assertTrue(exception.getMessage().contains("null"));
        }

        @Test
        @DisplayName("Email пользователя с некоренными символами")
        void shouldThrowWhenEmailHasNoAtSymbol() {
            // given
            var name = "test";
            var email = "testexample.com";
            var age = 40;

            // when
            var exception = assertThrows(ValidationException.class, () -> new UserDtoIn(name, email, age));

            // then
            assertTrue(exception.getMessage().contains("Некорректный формат email"));
        }

        @Test
        @DisplayName("Email пользователя с отсутствующим доменом")
        void shouldThrowWhenEmailHasNoDomain() {
            // given
            var name = "test";
            var email = "test@";
            var age = 40;

            // when
            var exception = assertThrows(ValidationException.class, () -> new UserDtoIn(name, email, age));

            // then
            assertTrue(exception.getMessage().contains("Некорректный формат email"));
        }

        @Test
        @DisplayName("Email пользователя с отсутствующим телом")
        void shouldThrowWhenEmailHasNoLocalPart() {
            // given
            var name = "test";
            var email = "@example.com";
            var age = 40;

            // when
            var exception = assertThrows(ValidationException.class, () -> new UserDtoIn(name, email, age));

            // then
            assertTrue(exception.getMessage().contains("Некорректный формат email"));
        }

        @Test
        @DisplayName("Email пользователя пробелом в почте")
        void shouldThrowWhenEmailHasSpaces() {
            // given
            var name = "test";
            var email = "test @example.com";
            var age = 40;

            // when
            var exception = assertThrows(ValidationException.class, () -> new UserDtoIn(name, email, age));

            // then
            assertTrue(exception.getMessage().contains("Некорректный формат email"));
        }

        @Test
        @DisplayName("Возраст пользователя null")
        void shouldThrowWhenAgeIsNull() {
            // given
            var name = "test";
            var email = "test@example.com";
            Integer age = null;

            // when
            var exception = assertThrows(ValidationException.class, () -> new UserDtoIn(name, email, age));

            // then
            assertEquals("Возраст не может быть null", exception.getMessage());
        }

        @Test
        @DisplayName("Возраст пользователя < 0")
        void shouldThrowWhenAgeIsNegative() {
            // given
            var name = "test";
            var email = "test@example.com";
            var age = -1;

            // when
            var exception = assertThrows(ValidationException.class, () -> new UserDtoIn(name, email, age));

            // then
            assertTrue(exception.getMessage().contains("Возраст должен быть в диапазоне от 0 до 120 лет"));
            assertTrue(exception.getMessage().contains("-1"));
        }

        @Test
        @DisplayName("Возраст пользователя больше валидного")
        void shouldThrowWhenAgeExceedsMaximum() {
            // given
            var name = "test";
            var email = "test@example.com";
            var age = 121;

            // when
            var exception = assertThrows(ValidationException.class, () -> new UserDtoIn(name, email, age));

            // then
            assertTrue(exception.getMessage().contains("Возраст должен быть в диапазоне от 0 до 120 лет"));
            assertTrue(exception.getMessage().contains("121"));
        }

        @Test
        @DisplayName("Успешное создание пользователя с возрастом 0")
        void shouldAcceptAgeZero() {
            // given
            var name = "test";
            var email = "test@example.com";
            var age = 0;

            // when
            var user = new UserDtoIn(name, email, age);

            // then
            assertEquals(0, user.age());
        }

        @Test
        @DisplayName("Успешное создание пользователя с макс допустимым возрастом")
        void shouldAcceptAgeMaximum() {
            // given
            var name = "test";
            var email = "test@example.com";
            var age = 120;

            // when
            var user = new UserDtoIn(name, email, age);

            // then
            assertEquals(120, user.age());
        }

        @Test
        @DisplayName("Некорректный формат email")
        void shouldRejectEmailWithDoubleAt() {
            // given
            var name = "test";
            var email = "user@@example.com";
            var age = 50;

            // when
            var exception = assertThrows(ValidationException.class, () -> new UserDtoIn(name, email, age));

            // then
            assertTrue(exception.getMessage().contains("Некорректный формат email"));
        }
    }
}