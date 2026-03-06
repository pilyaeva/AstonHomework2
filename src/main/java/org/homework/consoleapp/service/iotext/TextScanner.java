package org.homework.consoleapp.service.iotext;

public interface TextScanner {
    String readLine();
    int getIntInput(String prompt);
    long getLongInput(String prompt);
}
