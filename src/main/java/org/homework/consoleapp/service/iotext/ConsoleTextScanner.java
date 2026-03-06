package org.homework.consoleapp.service.iotext;

import java.util.Scanner;

public class ConsoleTextScanner implements TextScanner {
    private final Scanner scanner;
    private final TextPrinter textPrinter;

    public ConsoleTextScanner(Scanner scanner, TextPrinter textPrinter) {
        this.scanner = scanner;
        this.textPrinter = textPrinter;
    }

    @Override
    public int getIntInput(String prompt) {
        while (true) {
            textPrinter.print(prompt);
            try {
                var input = readLine();
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                textPrinter.println("Введите корректное число");
            }
        }
    }

    @Override
    public long getLongInput(String prompt) {
        while (true) {
            textPrinter.print(prompt);
            try {
                var input = readLine();
                return Long.parseLong(input);
            } catch (NumberFormatException e) {
                textPrinter.println("Введите корректное число");
            }
        }
    }

    @Override
    public String readLine() {
        return scanner.nextLine();
    }
}
