package train.booking.train.booking.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import train.booking.train.booking.model.enums.Route;
import train.booking.train.booking.model.enums.ScheduleType;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ScheduleRequestDTO {
    private Long scheduleId;

    private Long trainId;
    private String  trainName;

    private Long arrivalStationId;
    private Long departureStationId;

    private String arrivalStationName;
    private String departureStationName;


    private LocalDate departureDate;

    private LocalDate arrivalDate;

    private LocalTime departureTime;
    private LocalTime arrivalTime;


//    private String duration;
    private ScheduleType scheduleType;

    private Route route;
//    private String distance;
    private List<PriceListDTO> prices;


}
