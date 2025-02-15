package train.booking.train.booking.exceptions;

public class StationCannotBeFoundException extends RuntimeException{
    public StationCannotBeFoundException(String mesaage) {
        super(mesaage);
    }
}
