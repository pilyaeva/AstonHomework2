package org.homework.consoleapp.service.iotext;

public class ConsoleTextPrinter implements TextPrinter {
    @Override
    public void print(String message) {
        System.out.print(message);
    }

    @Override
    public void println(String message) {
        System.out.println(message);
    }
}