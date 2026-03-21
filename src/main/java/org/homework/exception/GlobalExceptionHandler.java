package org.homework.exception;

import com.fasterxml.jackson.databind.exc.ValueInstantiationException;
import jakarta.servlet.http.HttpServletRequest;
import org.homework.model.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BusinessLogicException.class)
    public ResponseEntity<ErrorResponse> handleBusinessLogicException(
            BusinessLogicException ex, HttpServletRequest request) {

        return buildBusinessLogicResponse(ex, request);
    }

    @ExceptionHandler({ValueInstantiationException.class, HttpMessageNotReadableException.class})
    public ResponseEntity<ErrorResponse> handleJsonProcessingException(
            Exception ex, HttpServletRequest request) {

        var cause = ex.getCause().getCause();

        if (cause instanceof BusinessLogicException businessEx) {
            logger.warn("Ошибка валидации при десериализации: {}", businessEx.getMessage());
            return buildBusinessLogicResponse(businessEx, request);
        }

        logger.warn("Ошибка чтения запроса: {}", ex.getMessage());
        return ResponseEntity
                .badRequest()
                .body(ErrorResponse.of(
                        "Invalid JSON",
                        "Неверный формат запроса или данные не прошли валидацию: " + ex.getMessage()
                ));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(
            IllegalArgumentException ex, HttpServletRequest request) {

        logger.warn("Некорректный аргумент: {}", ex.getMessage());

        return ResponseEntity
                .badRequest()
                .body(ErrorResponse.of(
                        "Bad Request",
                        "Некорректные данные: " + ex.getMessage()
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(
            Exception ex, HttpServletRequest request) {

        logger.error("Критическая ошибка на {}", request.getRequestURI(), ex);

        return ResponseEntity
                .status(500)
                .body(ErrorResponse.of(
                        "Internal Server Error",
                        "Произошла непредвиденная ошибка"
                ));
    }

    private ResponseEntity<ErrorResponse> buildBusinessLogicResponse(
            BusinessLogicException ex, HttpServletRequest request) {

        logger.warn("{}: {}", ex.getTitle(), ex.getMessage());

        return ResponseEntity
                .badRequest()
                .body(ErrorResponse.of(
                        ex.getTitle(),
                        ex.getMessage()
                ));
    }
}