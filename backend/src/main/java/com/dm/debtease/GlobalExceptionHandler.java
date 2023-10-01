package com.dm.debtease;

import com.dm.debtease.exception.InvalidFileFormatException;
import com.dm.debtease.model.APIError;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException.BadRequest;
import org.springframework.web.client.HttpClientErrorException.Forbidden;
import org.springframework.web.client.HttpClientErrorException.Unauthorized;
import org.springframework.web.client.HttpServerErrorException.InternalServerError;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler({BadRequest.class, UsernameNotFoundException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<APIError> handleBadRequestException(Exception ex) {
        APIError error = APIError.builder()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .time(LocalDateTime.now())
                .message("Bad Request")
                .description(ex.getMessage())
                .build();

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({Unauthorized.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<APIError> handleUnauthorizedException(Exception ex) {
        APIError error = APIError.builder()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .time(LocalDateTime.now())
                .message("Unauthorized")
                .description(ex.getMessage())
                .build();

        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler({Forbidden.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<APIError> handleForbiddenException(Exception ex) {
        APIError error = APIError.builder()
                .statusCode(HttpStatus.FORBIDDEN.value())
                .time(LocalDateTime.now())
                .message("Forbidden")
                .description(ex.getMessage())
                .build();

        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler({EntityNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<APIError> handleNotFoundException(Exception ex) {
        APIError error = APIError.builder()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .time(LocalDateTime.now())
                .message("Not Found")
                .description(ex.getMessage())
                .build();

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({InternalServerError.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<APIError> handleInternalServerError(Exception ex) {
        APIError error = APIError.builder()
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .time(LocalDateTime.now())
                .message("Internal Server Error")
                .description(ex.getMessage())
                .build();

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({InvalidFileFormatException.class})
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ResponseEntity<APIError> handleInvalidFileFormatError(Exception ex) {
        APIError error = APIError.builder()
                .statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value())
                .time(LocalDateTime.now())
                .message("Invalid File Format")
                .description(ex.getMessage())
                .build();

        return new ResponseEntity<>(error, HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
