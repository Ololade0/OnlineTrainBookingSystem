package train.booking.train.booking.exceptions;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

//@AllArgsConstructor
//@NoArgsConstructor
public class AgeRangeExeption extends RuntimeException {
    public AgeRangeExeption(String message) {
        super(message);
    }
}
