package be.crismartens.financetracker;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AccountInfoNotFoundException.class)
    public ResponseEntity<AppError> handleAccountInfoNotFoundException(AccountInfoNotFoundException ex) {
        AppError error = new AppError(
                Instant.now(),
                404,
                "User Info not found",
                ex.getMessage()
        );
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    record AppError(Instant timestamp, int status, String error, String message) {}
}
