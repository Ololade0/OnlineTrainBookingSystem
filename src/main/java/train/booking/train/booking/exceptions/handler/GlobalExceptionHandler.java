package train.booking.train.booking.exceptions.handler;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.messaging.handler.annotation.support.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import train.booking.train.booking.exceptions.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({StationCannotBeFoundException.class, RoleException.class})
    public ResponseEntity<Map<String, Object>> handleStationNotFoundException(StationCannotBeFoundException ex) {
        return buildResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler({UserAlreadyExistException.class, GenderTypeException.class})
    public ResponseEntity<Map<String, Object>> handleUserAlreadyExistException(UserAlreadyExistException ex) {
        return buildResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(StationAlreadyExistException.class)
    public ResponseEntity<Map<String, Object>> handleStationAlreadyExistException(StationAlreadyExistException ex) {
        return buildResponse(ex.getMessage(), HttpStatus.FOUND);
    }

    @ExceptionHandler(PasswordDoesNotMatchException.class)
    public ResponseEntity<Map<String, Object>> handlePasswordDoesNotMatchException(PasswordDoesNotMatchException ex) {
        return buildResponse(ex.getMessage(), HttpStatus.EXPECTATION_FAILED);
    }

    @ExceptionHandler(InvalidIdNumber.class)
    public ResponseEntity<Map<String, Object>> handleInvalidIdNumber(InvalidIdNumber ex) {
        return buildResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IdNumberAlreadyExist.class)
    public ResponseEntity<Map<String, Object>> handleIdNumberAlreadyExist(IdNumberAlreadyExist ex) {
        return buildResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidPassengerTypeException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidPassengerType(InvalidPassengerTypeException ex) {
        return buildResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler({Exception.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        return buildResponse(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        Throwable cause = ex.getCause();

        if (cause instanceof InvalidFormatException invalidEx && invalidEx.getTargetType().isEnum()) {
            Class<?> enumType = invalidEx.getTargetType();
            Object[] enumConstants = enumType.getEnumConstants();

            String allowedValues = Arrays.stream(enumConstants)
                    .map(Object::toString)
                    .collect(Collectors.joining(", "));

            String message = String.format(
                    "Invalid value '%s' for %s. Allowed values are: [%s]",
                    invalidEx.getValue(), enumType.getSimpleName(), allowedValues
            );

            return buildResponse(message, HttpStatus.BAD_REQUEST);
        }

        return buildResponse("Invalid request body", HttpStatus.BAD_REQUEST);
    }
    // Utility method to reduce repetition
    private ResponseEntity<Map<String, Object>> buildResponse(String message, HttpStatus status) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("message", message);
        response.put("status", status.value());
        response.put("error", status.getReasonPhrase());
        return new ResponseEntity<>(response, status);
    }
}
