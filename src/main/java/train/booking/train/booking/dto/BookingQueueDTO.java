package train.booking.train.booking.dto;

import lombok.Builder;
import lombok.Data;
import train.booking.train.booking.model.enums.AgeRange;
import train.booking.train.booking.model.enums.TrainClass;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
public class BookingQueueDTO {
    private Long userId;
    private Long scheduleId;
    private AgeRange passengerType;
    private LocalDate travelDate;
    private LocalTime travelTime;
    private TrainClass trainClass;
    private int seatNumber;
    private BigDecimal totalFare;
    private String bookingNumber;



}
