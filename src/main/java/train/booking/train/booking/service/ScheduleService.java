package train.booking.train.booking.service;

import org.springframework.data.domain.Page;
import train.booking.train.booking.dto.ScheduleDTO;
import train.booking.train.booking.dto.response.BaseResponse;
import train.booking.train.booking.dto.response.ScheduleResponse;
import train.booking.train.booking.model.Schedule;
import train.booking.train.booking.model.enums.Route;

import java.time.LocalDate;
import java.util.List;


public interface ScheduleService {
    BaseResponse newSchedule(ScheduleDTO scheduleDto);

    Schedule findSchedulesById(Long scheduleId);

    ScheduleResponse findSchedule(Long departureId, Long arrivalStationId, LocalDate departureDate);


    Page<Schedule> findAllSchedules(int page, int size);

    BaseResponse updateSchedule(Long id, ScheduleDTO scheduleDTO);

    BaseResponse deleteSchedule(Long id);

    List<Schedule> findByRouteName(Route route);
}
