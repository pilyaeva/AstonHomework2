package consoleapp.model;

import org.homework.consoleapp.exception.ValidationException;
import org.homework.consoleapp.model.UserDtoIn;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserDtoInTests {

    @Test
    void constructor_shouldCreateValidUserWithCorrectData() {
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
    void constructor_shouldThrowWhenNameIsNull() {
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
    void constructor_shouldThrowWhenNameIsBlank() {
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
    void constructor_shouldThrowWhenNameIsEmpty() {
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
    void constructor_shouldThrowWhenEmailIsNull() {
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
    void constructor_shouldThrowWhenEmailHasNoAtSymbol() {
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
    void constructor_shouldThrowWhenEmailHasNoDomain() {
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
    void constructor_shouldThrowWhenEmailHasNoLocalPart() {
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
    void constructor_shouldThrowWhenEmailHasSpaces() {
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
    void constructor_shouldThrowWhenAgeIsNull() {
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
    void constructor_shouldThrowWhenAgeIsNegative() {
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
    void constructor_shouldThrowWhenAgeExceedsMaximum() {
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
    void constructor_shouldAcceptAgeZero() {
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
    void constructor_shouldAcceptAgeMaximum() {
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
    void constructor_shouldAllowUnderscoreAndHyphenInEmail() {
        // given
        var name = "test";
        var email = "user_name-test@sub-domain.example.com";
        var age = 50;

        // when
        var user = new UserDtoIn(name, email, age);

        // then
        assertEquals(email, user.email());
    }

    @Test
    void constructor_shouldRejectEmailWithDoubleAt() {
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