package train.booking.train.booking.exceptions;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserCannotBeFoundException extends RuntimeException {
    private int statusCode;

    public UserCannotBeFoundException(String message) {
        super(message);
    }

    public UserCannotBeFoundException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;

    }
}
