package org.homework.consoleapp.service;

import org.homework.consoleapp.exception.ValidationException;
import org.homework.consoleapp.service.iotext.TextPrinter;
import org.homework.consoleapp.service.iotext.TextScanner;
import org.homework.infrastructure.exception.InfrastructureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;

public class CrudUserMenuService {
    private static final Logger logger = LoggerFactory.getLogger(CrudUserMenuService.class);

    private final Scanner scanner;
    private final TextPrinter textPrinter;
    private final TextScanner textScanner;
    private final UserActionService userActionService;

    public CrudUserMenuService(Scanner scanner,
                               TextPrinter textPrinter,
                               TextScanner textScanner,
                               UserActionService userActionService) {
        this.scanner = scanner;
        this.textPrinter = textPrinter;
        this.textScanner = textScanner;
        this.userActionService = userActionService;
    }

    public void run() {
        logger.info("Приложение запущено");
        var running = true;

        while (running) {
            printMenu();
            var choice = textScanner.getIntInput("Выберите действие: ");

            try {
                switch (choice) {
                    case 1:
                        userActionService.createUser();
                        break;
                    case 2:
                        userActionService.viewAllUsers();
                        break;
                    case 3:
                        userActionService.updateUser();
                        break;
                    case 4:
                        userActionService.deleteUser();
                        break;
                    case 5:
                        running = false;
                        logger.info("Выход из приложения");
                        break;
                    default:
                        textPrinter.print("Некорректное действие");
                }
            } catch (ValidationException e) {
                textPrinter.print("Ошибка валидации: " + e.getMessage());
            } catch (InfrastructureException e) {
                textPrinter.print("Ошибка инфраструктуры: " + e.getMessage());
            } catch (Exception e) {
                textPrinter.print("Ошибка: " + e.getMessage());
                logger.error("Ошибка", e);
            }
        }
    }

    private void printMenu() {
        textPrinter.println("\n--- Меню управления пользователями ---");
        textPrinter.println("1. Создать");
        textPrinter.println("2. Просмотр всех");
        textPrinter.println("3. Обновить");
        textPrinter.println("4. Удалить");
        textPrinter.println("5. Выход");
    }
}