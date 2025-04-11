package train.booking.train.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import train.booking.train.booking.model.Schedule;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ScheduleResponse {
    private List<Schedule> schedules;

}
