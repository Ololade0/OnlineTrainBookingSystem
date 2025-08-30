package train.booking.train.booking.dto;

import lombok.*;
import train.booking.train.booking.model.enums.Route;
import train.booking.train.booking.model.enums.ScheduleType;

import java.time.LocalDate;
import java.time.LocalTime;

@Setter
@Getter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleResponseDTO {
    private Long id;
    private LocalDate departureDate;
    private LocalDate arrivalDate;
    private LocalTime departureTime;
    private LocalTime arrivalTime;
    private String duration;
    private String distance;
    private ScheduleType scheduleType;
    private Route route;
    private String trainName;
    private String arrivalStationName;
    private String departureStationName;
}
