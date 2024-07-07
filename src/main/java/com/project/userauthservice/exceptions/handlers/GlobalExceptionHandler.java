package com.project.userauthservice.exceptions.handlers;

import com.project.userauthservice.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler({
            UnverifiedEmailException.class,
            EmailVerificationTokenException.class,
            ExistingUserException.class,
            IncorrectPasswordException.class,
            NotExistingUserException.class
    })
    public ResponseEntity<ErrorResponse> handleCustomException(Exception ex) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR; // Default status

        if (ex instanceof UnverifiedEmailException) {
            status = HttpStatus.FORBIDDEN;
        } else if (ex instanceof EmailVerificationTokenException) {
            status = HttpStatus.BAD_REQUEST;
        } else if (ex instanceof ExistingUserException) {
            status = HttpStatus.CONFLICT;
        } else if (ex instanceof IncorrectPasswordException) {
            status = HttpStatus.UNAUTHORIZED;
        } else if (ex instanceof NotExistingUserException) {
            status = HttpStatus.NOT_FOUND;
        }

        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage());
        return new ResponseEntity<>(errorResponse, status);
    }
}
