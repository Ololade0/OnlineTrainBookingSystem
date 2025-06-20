package train.booking.train.booking.exceptions;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
//@AllArgsConstructor
//@NoArgsConstructor
public class SeatAlreadyReservedException extends RuntimeException {
    public SeatAlreadyReservedException(String message) {
        super(message);
    }
}
