package train.booking.train.booking.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import train.booking.train.booking.dto.PriceListDTO;
import train.booking.train.booking.dto.ScheduleDTO;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleResponse {
    private Long scheduleId;

        private LocalDate departureDate;
        private LocalTime departureTime;

    private List<ScheduleDTO> schedules;
    private List<PriceListDTO> prices = new ArrayList<>();


    public ScheduleResponse(Long id, List<ScheduleDTO> schedules, List<PriceListDTO> prices) {
        this.scheduleId = id;
        this.schedules = schedules;
        this.prices = prices;
    }


    public ScheduleResponse(Long id, List<ScheduleDTO> schedules) {
        this.scheduleId = id;
        this.schedules = schedules;

    }
}
