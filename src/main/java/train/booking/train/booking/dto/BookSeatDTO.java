package train.booking.train.booking.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import train.booking.train.booking.model.enums.TrainClass;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookSeatDTO {
    private int seatNumber;
    private TrainClass trainClass;

}
