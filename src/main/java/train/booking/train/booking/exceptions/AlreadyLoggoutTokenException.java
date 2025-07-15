package train.booking.train.booking.exceptions;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class AlreadyLoggoutTokenException extends RuntimeException {
    public AlreadyLoggoutTokenException(String message) {
        super(message);
    }
}
