package train.booking.train.booking.exceptions;

public class TrainCannotBeFoundException extends RuntimeException {
    public TrainCannotBeFoundException(String message) {
        super(message);
    }
}
