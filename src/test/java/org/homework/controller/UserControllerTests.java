package org.homework.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.homework.exception.GlobalExceptionHandler;
import org.homework.exception.ValidationException;
import org.homework.model.UserDtoIn;
import org.homework.model.UserDtoOut;
import org.homework.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = {UserController.class},
        includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = GlobalExceptionHandler.class))
@DisplayName("UserController. Тесты контроллера пользователей")
class UserControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    @DisplayName("createUser. Создание пользователя")
    class CreateUserTests {

        @Test
        @DisplayName("Создаёт пользователя и возвращает созданный объект")
        void shouldCreateUserAndReturnCreatedUser() throws Exception {
            // given
            var inputDto = new UserDtoIn("test1", "test1@test.com", 25);
            var now = LocalDateTime.now();
            var outputDto = new UserDtoOut(10L, "test1", "test1@test.com", 25, now);
            when(userService.createUser(any(UserDtoIn.class))).thenReturn(outputDto);

            // when & then
            mockMvc.perform(post("/user")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(inputDto)))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(10))
                    .andExpect(jsonPath("$.name").value("test1"))
                    .andExpect(jsonPath("$.age").value(25))
                    .andExpect(jsonPath("$.createdAt").exists());

            verify(userService, times(1)).createUser(any(UserDtoIn.class));
        }

        @Test
        @DisplayName("Возвращает 400 при некорректном email")
        void shouldReturnBadRequestWhenEmailIsInvalid() throws Exception {
            // given
            var invalidJson = """
                {
                    "name": "Test",
                    "email": "invalid-email",
                    "age": 25
                }
                """;
            when(userService.createUser(any(UserDtoIn.class)))
                    .thenThrow(new ValidationException("Некорректный формат email"));

            // when & then
            mockMvc.perform(post("/user")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(invalidJson))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Возвращает 400 при недопустимом возрасте")
        void shouldReturnBadRequestWhenAgeIsInvalid() throws Exception {
            // given
            var invalidJson = """
                {
                    "name": "Test",
                    "email": "test@test.com",
                    "age": 150
                }
                """;
            when(userService.createUser(any(UserDtoIn.class)))
                    .thenThrow(new ValidationException("Возраст должен быть в диапазоне от 0 до 120"));

            // when & then
            mockMvc.perform(post("/user")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(invalidJson))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Возвращает 400 при пустом имени")
        void shouldReturnBadRequestWhenNameIsBlank() throws Exception {
            // given
            var invalidJson = """
                {
                    "name": "   ",
                    "email": "test@test.com",
                    "age": 25
                }
                """;
            when(userService.createUser(any(UserDtoIn.class)))
                    .thenThrow(new ValidationException("Имя пользователя не может быть пустым"));

            // when & then
            mockMvc.perform(post("/user")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(invalidJson))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("getById. Получение пользователя по ID")
    class GetByIdTests {

        @Test
        @DisplayName("Возвращает пользователя по существующему ID")
        void shouldReturnUserWhenExists() throws Exception {
            // given
            var userId = 5L;
            var createdAt = LocalDateTime.of(2023, 10, 25, 14, 30);
            var outputDto = new UserDtoOut(userId, "test3", "test3@test.com", 30, createdAt);
            when(userService.getById(userId)).thenReturn(outputDto);

            // when & then
            mockMvc.perform(get("/user/{id}", userId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(userId))
                    .andExpect(jsonPath("$.name").value("test3"))
                    .andExpect(jsonPath("$.age").value(30))
                    .andExpect(jsonPath("$.createdAt").value("2023-10-25T14:30:00"));
        }
    }

    @Nested
    @DisplayName("getAllUsers. Получение всех пользователей")
    class GetAllUsersTests {

        @Test
        @DisplayName("Возвращает список всех пользователей")
        void shouldReturnAllUsers() throws Exception {
            // given
            var users = List.of(
                    new UserDtoOut(1L, "test2", "test2@test.com", 25, LocalDateTime.now()),
                    new UserDtoOut(2L, "Jane", "jane@test.com", 30, LocalDateTime.now())
            );
            when(userService.getAllUsers()).thenReturn(users);

            // when & then
            mockMvc.perform(get("/user"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(2));
        }
    }

    @Nested
    @DisplayName("updateUser. Обновление пользователя")
    class UpdateUserTests {

        @Test
        @DisplayName("Обновляет существующего пользователя и возвращает обновлённый объект")
        void shouldUpdateExistingUserAndReturnUpdatedUser() throws Exception {
            // given
            var userId = 1L;
            var inputDto = new UserDtoIn("test1 Updated", "new@test.com", 26);
            var outputDto = new UserDtoOut(userId, "test1 Updated", "new@test.com", 26, LocalDateTime.now());
            when(userService.updateUser(eq(userId), any(UserDtoIn.class))).thenReturn(outputDto);

            // when & then
            mockMvc.perform(put("/user/{id}", userId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(inputDto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("test1 Updated"))
                    .andExpect(jsonPath("$.age").value(26));

            verify(userService).updateUser(eq(userId), any(UserDtoIn.class));
        }
    }

    @Nested
    @DisplayName("deleteUser. Удаление пользователя")
    class DeleteUserTests {

        @Test
        @DisplayName("Удаляет пользователя и возвращает 204 No Content")
        void shouldDeleteUserAndReturnNoContent() throws Exception {
            // given
            var userId = 5L;
            doNothing().when(userService).deleteUser(userId);

            // when & then
            mockMvc.perform(delete("/user/{id}", userId))
                    .andExpect(status().isNoContent());

            verify(userService).deleteUser(userId);
        }
    }
}