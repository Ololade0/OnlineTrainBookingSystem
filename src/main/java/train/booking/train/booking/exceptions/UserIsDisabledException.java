package train.booking.train.booking.exceptions;

public class UserIsDisabledException extends RuntimeException {
    public UserIsDisabledException(String message){
        super(message);
    }
}
