package train.booking.train.booking.dto;

import train.booking.train.booking.model.enums.Route;
import train.booking.train.booking.model.enums.ScheduleType;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;

public interface ScheduleDetailsDTO {
    Long getScheduleId();
    Long getTrainId();
    LocalTime getDepartureTime();
    LocalTime getArrivalTime();
    LocalDate getDepartureDate();
    LocalDate getArrivalDate();
    Duration getDuration();
    String getDistance();
    ScheduleType getScheduleType();
    Route getRoute();
    String getDepartureStation();
    String getArrivalStation();
    String getTrainClass();
    Double getPrice();
    String getAgeRange();
}
