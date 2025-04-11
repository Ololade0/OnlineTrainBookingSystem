package train.booking.train.booking.exceptions.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import train.booking.train.booking.exceptions.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(StationCannotBeFoundException.class)
    public ResponseEntity<Map<String, Object>> handleStationNotFoundException(StationCannotBeFoundException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("message", ex.getMessage());
        response.put("status", HttpStatus.NOT_FOUND.value());
        response.put("error", "Station Not Found");

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(UserAlreadyExistException.class)
    public ResponseEntity<Map<String, Object>> handleUserAlreadyExistException(UserAlreadyExistException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("message", ex.getMessage());
        response.put("status", HttpStatus.BAD_REQUEST.value());
//        response.put("error", "");

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(StationAlreadyExistException.class)
    public ResponseEntity<Map<String, Object>> handleStationAlreadyExistException(StationAlreadyExistException exception){
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("message", exception.getMessage());
        response.put("status", HttpStatus.BAD_REQUEST.value());
//        response.put("error", "Station Already Exist");
        return new ResponseEntity<>(response, HttpStatus.FOUND);


    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("message", ex.getMessage());
        response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.put("error", "Internal Server Error");

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler(PasswordDoesNotMatchException.class)
    public ResponseEntity<Map<String, Object>> handlePasswordDoesNotMatchException(PasswordDoesNotMatchException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("message", ex.getMessage());
        response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
//        response.put("error", "The passwords you entered do not match.");

        return new ResponseEntity<>(response, HttpStatus.EXPECTATION_FAILED);
    }

    @ExceptionHandler(InvalidIdNumber.class)
    public ResponseEntity<Map<String, Object>> handleInvalidIdNumber(InvalidIdNumber ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("message", ex.getMessage());
        response.put("status", HttpStatus.BAD_REQUEST.value());
//        response.put("error", "ID number must be between 10 and 15 characters.");

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(IdNumberAlreadyExist.class)
    public ResponseEntity<Map<String, Object>> handleIdNumberAlreadyExist(IdNumberAlreadyExist ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("message", ex.getMessage());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "user identification already exist.");

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidPassengerTypeException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidPassengerType(InvalidPassengerTypeException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("message", ex.getMessage());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }
}

