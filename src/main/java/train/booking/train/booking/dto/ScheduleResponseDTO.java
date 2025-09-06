package train.booking.train.booking.dto;

import lombok.*;
import train.booking.train.booking.model.enums.Route;
import train.booking.train.booking.model.enums.ScheduleType;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Setter
@Getter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleResponseDTO {
    private Long id;
    private Long trainId;
    private String trainName;
    private Long departureStationId;
    private String departureStationName;
    private Long arrivalStationId;
    private String arrivalStationName;
    private LocalDate departureDate;
    private LocalDate arrivalDate;
    private LocalTime departureTime;
    private LocalTime arrivalTime;
    private ScheduleType scheduleType;
    private Route route;
    private String duration;   // computed
    private String distance;   // computed
    private List<PriceListDTO> prices;

    // getters & setters
}

//public class ScheduleResponseDTO {
//    private Long id;
//    private LocalDate departureDate;
//    private LocalDate arrivalDate;
//    private LocalTime departureTime;
//    private LocalTime arrivalTime;
//    private String duration;
//    private String distance;
//    private ScheduleType scheduleType;
//    private Route route;
//    private String trainName;
//    private String arrivalStationName;
//    private String departureStationName;
//}
