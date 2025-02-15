package train.booking.train.booking.exceptions;


public class InvalidSeatNumberException extends RuntimeException {
    public InvalidSeatNumberException(String message) {
        super(message);
    }
}