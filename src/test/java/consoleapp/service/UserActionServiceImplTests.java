package consoleapp.service;

import org.homework.consoleapp.controller.UserController;
import org.homework.consoleapp.model.UserDtoIn;
import org.homework.consoleapp.model.UserDtoOut;
import org.homework.consoleapp.service.UserActionServiceImpl;
import org.homework.consoleapp.service.iotext.TextPrinter;
import org.homework.consoleapp.service.iotext.TextScanner;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserActionServiceImplTests {

    @Mock
    private UserController userController;

    @Mock
    private TextPrinter textPrinter;

    @Mock
    private TextScanner textScanner;

    @InjectMocks
    private UserActionServiceImpl userActionService;

    @Test
    void createUser_shouldReadInputsCreateDtoAndPrintSuccess() {
        // given
        var name = "test";
        var email = "test@example.com";
        var age = 25;

        when(textScanner.readLine()).thenReturn(name, email);
        when(textScanner.getIntInput("Введите возраст: ")).thenReturn(age);

        // when
        userActionService.createUser();

        // then
        verify(userController).createUser(any(UserDtoIn.class));
        verify(textPrinter).println("Пользователь создан");
    }

    @Test
    void viewAllUsers_shouldNotPrintUsersWhenListIsEmpty() {
        // given
        when(userController.getAllUsers()).thenReturn(List.of());

        // when
        userActionService.viewAllUsers();

        // then
        verify(textPrinter).println("Пользователей нет");
    }

    @Test
    void viewAllUsers_shouldPrintAllUsersWhenUsersExist() {
        // given
        var user1 = new UserDtoOut(1L, "test1", "test1@example.com", 20, null);
        var user2 = new UserDtoOut(2L, "test2", "test2@example.com", 30, null);

        when(userController.getAllUsers()).thenReturn(List.of(user1, user2));

        // when
        userActionService.viewAllUsers();

        // then
        verify(userController).getAllUsers();
        verify(textPrinter).println(user1.toString());
        verify(textPrinter).println(user2.toString());
        verify(textPrinter, never()).println("Пользователей нет");
    }

    @Test
    void updateUser_shouldUpdateUserAndPrintSuccessWhenUserExists() {
        // given
        var id = 1L;
        var name = "test";
        var email = "test@example.com";
        var age = 25;

        when(textScanner.getLongInput("Введите ID пользователя: ")).thenReturn(id);
        when(textScanner.readLine()).thenReturn(name, email);
        when(textScanner.getIntInput("Введите возраст: ")).thenReturn(age);
        when(userController.updateUser(eq(id), any(UserDtoIn.class))).thenReturn(true);

        // when
        userActionService.updateUser();

        // then
        verify(userController).updateUser(eq(id), any(UserDtoIn.class));
        verify(textPrinter).println("Пользователь обновлён");
        verify(textPrinter, never()).println("Пользователь не найден, обновление невозможно");
    }

    @Test
    void updateUser_shouldPrintNotFoundWhenUserDoesNotExist() {
        // given
        var id = 999L;
        var name = "test";
        var email = "test@example.com";
        var age = 25;

        when(textScanner.getLongInput("Введите ID пользователя: ")).thenReturn(id);
        when(textScanner.readLine()).thenReturn(name, email);
        when(textScanner.getIntInput("Введите возраст: ")).thenReturn(age);
        when(userController.updateUser(eq(id), any(UserDtoIn.class))).thenReturn(false);

        // when
        userActionService.updateUser();

        // then
        verify(userController).updateUser(eq(id), any(UserDtoIn.class));
        verify(textPrinter).println("Пользователь не найден, обновление невозможно");
        verify(textPrinter, never()).println("Пользователь обновлён");
    }

    @Test
    void deleteUser_shouldDeleteUserAndPrintSuccessWhenUserExists() {
        // given
        var id = 1L;

        when(textScanner.getLongInput("Введите ID пользователя: ")).thenReturn(id);
        when(userController.deleteUser(id)).thenReturn(true);

        // when
        userActionService.deleteUser();

        // then
        verify(userController).deleteUser(id);
        verify(textPrinter).println("Пользователь удалён");
        verify(textPrinter, never()).println("Пользователь не найден");
    }

    @Test
    void deleteUser_shouldPrintNotFoundWhenUserDoesNotExist() {
        // given
        var id = 404L;

        when(textScanner.getLongInput("Введите ID пользователя: ")).thenReturn(id);
        when(userController.deleteUser(id)).thenReturn(false);

        // when
        userActionService.deleteUser();

        // then
        verify(userController).deleteUser(id);
        verify(textPrinter).println("Пользователь не найден");
        verify(textPrinter, never()).println("Пользователь удалён");
    }

    @Test
    void createUser_shouldPassCorrectDtoToController() {
        // given
        var name = "test";
        var email = "test@example.com";
        var age = 30;

        when(textScanner.readLine()).thenReturn(name, email);
        when(textScanner.getIntInput("Введите возраст: ")).thenReturn(age);

        // when
        userActionService.createUser();

        // then
        verify(userController).createUser(argThat(dto ->
                dto.name().equals(name) &&
                        dto.email().equals(email) &&
                        dto.age().equals(age)
        ));
    }

    @Test
    void updateUser_shouldPassCorrectDtoAndIdToController() {
        // given
        var id = 5L;
        var name = "test";
        var email = "test@example.com";
        var age = 40;

        when(textScanner.getLongInput("Введите ID пользователя: ")).thenReturn(id);
        when(textScanner.readLine()).thenReturn(name, email);
        when(textScanner.getIntInput("Введите возраст: ")).thenReturn(age);
        when(userController.updateUser(eq(id), any(UserDtoIn.class))).thenReturn(true);

        // when
        userActionService.updateUser();

        // then
        verify(userController).updateUser(eq(id), argThat(dto ->
                dto.name().equals(name) &&
                        dto.email().equals(email) &&
                        dto.age().equals(age)
        ));
    }
}