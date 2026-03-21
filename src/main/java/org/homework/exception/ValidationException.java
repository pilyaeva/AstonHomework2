package org.homework.exception;

public class ValidationException extends BusinessLogicException {

    public ValidationException(String message) {
        super("Ошибка валидации", message);
    }
}