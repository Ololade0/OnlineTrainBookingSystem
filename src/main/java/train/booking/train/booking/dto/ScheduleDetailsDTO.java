package train.booking.train.booking.dto;

import train.booking.train.booking.model.enums.AgeRange;
import train.booking.train.booking.model.enums.Route;
import train.booking.train.booking.model.enums.ScheduleType;
import train.booking.train.booking.model.enums.TrainClass;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public interface ScheduleDetailsDTO {
    Long getScheduleId();
    Long getTrainId();
    LocalTime getDepartureTime();
    LocalTime getArrivalTime();
    LocalDate getDepartureDate();
    LocalDate getArrivalDate();
    String getDuration();
    String getDistance();
    ScheduleType getScheduleType();
    Route getRoute();
    Long getDepartureStation();
    Long getArrivalStation();
    TrainClass getTrainClass();
    BigDecimal getPrice();
    AgeRange getAgeRange();
}
