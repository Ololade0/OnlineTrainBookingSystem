package train.booking.train.booking.service;

import org.springframework.data.domain.Page;
import train.booking.train.booking.dto.ScheduleDTO;
import train.booking.train.booking.dto.response.BaseResponse;
import train.booking.train.booking.dto.response.ScheduleResponse;
import train.booking.train.booking.model.Schedule;

import java.time.LocalDate;


public interface ScheduleService {
    BaseResponse newSchedule(ScheduleDTO scheduleDto);

    Schedule findSchedulesById(Long scheduleId);

    ScheduleResponse findSchedule(Long departureId, Long arrivalStationId, LocalDate departureDate);


    Page<Schedule> findAllSchedules(int page, int size);
}
