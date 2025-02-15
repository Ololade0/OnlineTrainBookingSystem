package train.booking.train.booking.exceptions;

public class ScheduleCannotBeFoundException extends RuntimeException {
    public ScheduleCannotBeFoundException(String message) {
        super(message);
    }
}
