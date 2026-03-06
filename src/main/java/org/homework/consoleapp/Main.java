package org.homework.consoleapp;

import org.homework.consoleapp.controller.UserController;
import org.homework.consoleapp.service.iotext.ConsoleTextPrinter;
import org.homework.consoleapp.service.CrudUserMenuService;
import org.homework.consoleapp.service.UserActionServiceImpl;
import org.homework.consoleapp.service.iotext.ConsoleTextScanner;
import org.homework.infrastructure.repository.UserRepositoryImlp;
import org.homework.infrastructure.util.HibernateUtil;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        var scanner = new Scanner(System.in);
        var textPrinter = new ConsoleTextPrinter();
        var textScanner = new ConsoleTextScanner(scanner, textPrinter);
        var userRepository = new UserRepositoryImlp();
        var userController = new UserController(userRepository);
        var userActionService = new UserActionServiceImpl(userController, textPrinter, textScanner);

        var menuService = new CrudUserMenuService(scanner, textPrinter, textScanner, userActionService);

        try {
            menuService.run();
        } finally {
            HibernateUtil.shutdown();
            scanner.close();
        }
    }
}