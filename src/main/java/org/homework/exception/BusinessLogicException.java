package org.homework.exception;

public class BusinessLogicException extends RuntimeException {
    private final String title;
    private final String message;

    public BusinessLogicException(String title, String message) {
        super(message);
        this.title = title;
        this.message = message;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
