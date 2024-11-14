package com.moviedb_api.exception;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.moviedb_api.service.MovieService;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(MovieService.class);


    // Handle ResourceNotFoundException (404)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleResourceNotFoundException(
            ResourceNotFoundException ex) {
        ExceptionResponse response = new ExceptionResponse(HttpStatus.NOT_FOUND.value(),
                ex.getMessage(), "RESOURCE_NOT_FOUND");
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    // Handle ResourceAlreadyExistsException (409)
    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ExceptionResponse> handleResourceAlreadyExistsException(
            ResourceAlreadyExistsException ex) {
        ExceptionResponse response = new ExceptionResponse(HttpStatus.CONFLICT.value(),
                ex.getMessage(), "RESOURCE_ALREADY_EXISTS");
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    // Handle BadRequestException (400)
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ExceptionResponse> handleBadRequestException(BadRequestException ex) {
        ExceptionResponse response = new ExceptionResponse(HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(), "BAD_REQUEST");
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // Handle HttpMessageNotReadableException
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ExceptionResponse> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException ex) {
        ExceptionResponse response = new ExceptionResponse(HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(), "INVALID_REQUEST_PAYLOAD");
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // Handle validation exceptions (400)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            fieldErrors.put(fieldName, errorMessage);
        });

        ExceptionResponse response = new ExceptionResponse(HttpStatus.BAD_REQUEST.value(),
                "Validation failed", "VALIDATION_ERROR");
        response.setFieldErrors(fieldErrors);

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // Handle invalid format exception (400)
    @ExceptionHandler(InvalidFormatException.class)
    public ResponseEntity<ExceptionResponse> handleInvalidFormatException(
            InvalidFormatException ex) {
        ExceptionResponse response = new ExceptionResponse(HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(), "INVALID_FORMAT");

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // Handle generic exceptions (500)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleGenericException(Exception ex) {
        logger.info("Generic exception: " + ex);
        ExceptionResponse response = new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                ex.getMessage(), "INTERNAL_SERVER_ERROR", ex.getCause().toString());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
