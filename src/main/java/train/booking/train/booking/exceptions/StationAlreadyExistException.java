package train.booking.train.booking.exceptions;

public class StationAlreadyExistException extends RuntimeException {
    public StationAlreadyExistException(String message) {
        super(message);
    }
}
