package train.booking.train.booking.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import train.booking.train.booking.model.Seat;
import train.booking.train.booking.model.TrainClass;
import train.booking.train.booking.model.enums.SeatStatus;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SeatDto {
    private int startSeat;
    private int endSeat;
    private SeatStatus seatStatus;
    private int seatNumber;
    private TrainClass trainClass;


    public void setSeatNumber(int seatNumber) {
    }

    public void setStatus(SeatStatus status) {
    }
}
