package train.booking.train.booking.dto;

import lombok.Builder;
import lombok.Data;
import train.booking.train.booking.model.enums.TrainClass;

import java.time.LocalDateTime;

@Data
@Builder
public class BookingQueueDTO {
    private Long userId;
    private Long scheduleId;
    private LocalDateTime travelDate;
    private TrainClass trainClass;
    private int seatNumber;
    private String bookingNameRecord;
}
