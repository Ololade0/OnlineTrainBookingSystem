package train.booking.train.booking.exceptions;

public class SeatCannotBeFoundException extends RuntimeException{
    public SeatCannotBeFoundException(String message) {
        super(message);
    }
}
