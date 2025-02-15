package train.booking.train.booking.exceptions;

public class UserCannotBeFoundException extends RuntimeException {
    public UserCannotBeFoundException(String message) {
        super(message);
    }
}
