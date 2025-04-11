package train.booking.train.booking.service;

import train.booking.train.booking.dto.ScheduleDTO;
import train.booking.train.booking.dto.ScheduleResponse;
import train.booking.train.booking.dto.response.BaseResponse;
import train.booking.train.booking.model.Schedule;

import java.util.Date;
import java.util.List;

public interface ScheduleService{
    BaseResponse newSchedule(ScheduleDTO scheduleDto);


    BaseResponse findScheduleById(Long scheduleId);

//    List<Schedule> findScheduleByStations(String to, String from, Date date);

    Schedule findSchedulesById(Long scheduleId);

    public ScheduleResponse findSchedule(ScheduleDTO findScheduleDTO);



}