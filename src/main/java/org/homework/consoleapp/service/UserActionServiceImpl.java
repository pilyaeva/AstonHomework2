package org.homework.consoleapp.service;

import org.homework.consoleapp.controller.UserController;
import org.homework.consoleapp.model.UserDtoIn;
import org.homework.consoleapp.service.iotext.TextPrinter;
import org.homework.consoleapp.service.iotext.TextScanner;

public class UserActionServiceImpl implements UserActionService {

    private final UserController userController;
    private final TextPrinter textPrinter;
    private final TextScanner textScanner;

    public UserActionServiceImpl(UserController userController, TextPrinter textPrinter, TextScanner textScanner) {
        this.userController = userController;
        this.textPrinter = textPrinter;
        this.textScanner = textScanner;
    }

    @Override
    public void createUser() {
        textPrinter.print("Введите имя: ");
        var name = textScanner.readLine();

        textPrinter.print("Введите email: ");
        var email = textScanner.readLine();

        var age = textScanner.getIntInput("Введите возраст: ");

        var userDto = new UserDtoIn(name, email, age);

        userController.createUser(userDto);

        textPrinter.println("Пользователь создан");
    }

    @Override
    public void viewAllUsers() {
        var users = userController.getAllUsers();

        if (users.isEmpty()) {
            textPrinter.println("Пользователей нет");
            return;
        }

        users.forEach(userDto -> textPrinter.println(userDto.toString()));
    }

    @Override
    public void updateUser() {
        var id = textScanner.getLongInput("Введите ID пользователя: ");

        textPrinter.print("Введите имя: ");
        var name = textScanner.readLine();

        textPrinter.print("Введите email: ");
        var email = textScanner.readLine();

        var age = textScanner.getIntInput("Введите возраст: ");

        var userDto = new UserDtoIn(name, email, age);

        boolean success = userController.updateUser(id, userDto);

        if (success) {
            textPrinter.println("Пользователь обновлён");
        } else {
            textPrinter.println("Пользователь не найден, обновление невозможно");
        }
    }

    @Override
    public void deleteUser() {
        var id = textScanner.getLongInput("Введите ID пользователя: ");

        boolean success = userController.deleteUser(id);

        if (success) {
            textPrinter.println("Пользователь удалён");
        } else {
            textPrinter.println("Пользователь не найден");
        }
    }
}