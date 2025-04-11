package train.booking.train.booking.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import train.booking.train.booking.model.Station;
import train.booking.train.booking.model.enums.Route;
import train.booking.train.booking.model.enums.ScheduleType;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ScheduleDTO {

    private String arrivalStationName;
    private String departureStationName;


    private LocalTime departureTime;

    private LocalTime arrivalTime;

    private LocalDate departureDate;

    private LocalDate arrivalDate;
    private Duration duration;
    private ScheduleType scheduleType;

    private Route route;
    private String distance;

    private Long trainId;
    public Long stationId;


}
