package train.booking.train.booking.dto;

import lombok.Data;
import train.booking.train.booking.model.enums.TrainClass;

@Data
public class BookingRequestDTO {
    private Long userId;
    private Long scheduleId;
    private TrainClass trainClass;
    private int seatNumber;
}