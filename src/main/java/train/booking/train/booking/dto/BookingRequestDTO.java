package train.booking.train.booking.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import train.booking.train.booking.dto.response.ScheduleResponse;
import train.booking.train.booking.model.enums.AgeRange;
import train.booking.train.booking.model.enums.TrainClass;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookingRequestDTO {
    private Long userId;
    private Long scheduleId;
    private Long departureStationId;
    private Long arrivalStationId;
    private LocalDate departureDate;
    private LocalTime departureTime;
    private TrainClass trainClass;
    private int seatNumber;
    private AgeRange passengerType;


   private ScheduleResponse scheduleResponse;
   private String bookingNumber;
    private BigDecimal totalFare;


}