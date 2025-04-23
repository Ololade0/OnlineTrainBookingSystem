package train.booking.train.booking.service.Impl;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import train.booking.train.booking.dto.ScheduleDTO;
import train.booking.train.booking.dto.ScheduleDetailsDTO;
import train.booking.train.booking.dto.ScheduleResponse;
import train.booking.train.booking.dto.response.BaseResponse;
import train.booking.train.booking.dto.response.ResponseUtil;
import train.booking.train.booking.exceptions.ScheduleCannotBeFoundException;
import train.booking.train.booking.exceptions.ScheduleDetailsException;
import train.booking.train.booking.model.Schedule;
import train.booking.train.booking.model.Station;
import train.booking.train.booking.model.Train;
import train.booking.train.booking.model.enums.Route;
import train.booking.train.booking.model.enums.StationType;
import train.booking.train.booking.repository.ScheduleRepository;
import train.booking.train.booking.service.*;

import java.time.LocalDate;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {

     private final ScheduleRepository scheduleRepository;
    private final DistanceCalculatorService distanceCalculatorService;
    private final TrainService trainService;
    private final StationService stationService;
//    private final StopService stopService;
@Transactional
    public BaseResponse newSchedule(ScheduleDTO scheduleDto) {
       validateScheduleDetails(scheduleDto);
        try {
           Train train = trainService.findTrainById(scheduleDto.getTrainId());
           Station arrivalStationId = stationService.findStationById(scheduleDto.getArrivalStationId());
            Station departureStationId = stationService.findStationById(scheduleDto.getDepartureStationId());

            Schedule schedule = Schedule.builder()
                    .trainId(train.getId())
                    .departureStationId(departureStationId.getStationId())
                    .arrivalStationId(arrivalStationId.getStationId())
                    .departureTime(scheduleDto.getDepartureTime())
                    .arrivalTime(scheduleDto.getArrivalTime())
                    .departureDate(scheduleDto.getDepartureDate())
                    .arrivalDate(scheduleDto.getArrivalDate())
                    .duration(scheduleDto.getDuration())
                    .scheduleType(scheduleDto.getScheduleType())
                    .route(scheduleDto.getRoute())
                    .distance(distanceCalculatorService.calculateDistance())
                    .build();
            Schedule savedSchedule = scheduleRepository.save(schedule);
            log.info("SAVED SCHEDULES: {}", savedSchedule);

//            scheduleRepository.save(savedSchedule);
            return mapToResponseDTO(savedSchedule, "Schedule successfully created");
        } catch (Exception e) {
            log.error("Error creating schedule: {}", e.getMessage(), e);
            return ResponseUtil.failed("Failed to create schedule", e);
        }
    }





    private static BaseResponse mapToResponseDTO(Schedule savedSchedule, String message) {
        ScheduleDTO response = new ScheduleDTO();
        response.setDepartureTime(savedSchedule.getDepartureTime());
        response.setArrivalTime(savedSchedule.getArrivalTime());
        response.setDepartureDate(savedSchedule.getDepartureDate());
        response.setArrivalDate(savedSchedule.getArrivalDate());
        response.setDuration(savedSchedule.getDuration());
        response.setDistance(savedSchedule.getDistance());
        response.setScheduleType(savedSchedule.getScheduleType());
        response.setRoute(savedSchedule.getRoute());
        return ResponseUtil.success(message, response);
    }



    public void validateScheduleDetails(ScheduleDTO scheduleDTO) {
        // Ensure all required fields are provided
        if (scheduleDTO.getDepartureStationId() == null || scheduleDTO.getArrivalStationId() == null ||
                scheduleDTO.getScheduleType() == null || scheduleDTO.getDepartureDate() == null ||
                scheduleDTO.getDepartureTime() == null) {
            throw new ScheduleDetailsException("All parameters must be provided and cannot be null.");
        }

        // Ensure arrival time is not before departure time
        if (scheduleDTO.getArrivalTime().isBefore(scheduleDTO.getDepartureTime())) {
            throw new ScheduleDetailsException("Arrival time cannot be before departure time.");
        }

        if(scheduleDTO.getDepartureTime().equals(scheduleDTO.getArrivalTime())){
            throw new ScheduleDetailsException("Departure and arrival time must be different.");
        }

        // Ensure departure and arrival stations are different
        if (scheduleDTO.getDepartureStationId().equals(scheduleDTO.getArrivalStationId())) {
            throw new ScheduleDetailsException("Departure and arrival stations must be different.");
        }
        if((scheduleDTO.getRoute() == Route.IBADAN_LAGOS_AFTERNOON_TRAIN ||
                scheduleDTO.getRoute()== Route.LAGOS_IBADAN_AFTERNOON_TRAIN ||
                scheduleDTO.getRoute()==Route.LAGOS_IBADAN_MORNING_TRAIN ||
                scheduleDTO.getRoute()==Route.IBADAN_LAGOS_MORNING_TRAIN)
                        && !scheduleDTO.getDepartureDate().equals(scheduleDTO.getArrivalDate())){
            throw new ScheduleDetailsException("Departure Date must not be different.");


        }




    }



//
//    @Override
//    public BaseResponse findScheduleById(Long scheduleId) {
//        log.info("Fetching schedule with ID: {}", scheduleId);
//        Schedule foundSchedule = scheduleRepository.findById(scheduleId)
//                .orElseThrow(() -> new ScheduleCannotBeFoundException("Schedule not found"));
//                            log.error("Schedule not found for ID: {}", scheduleId);
//        return mapToResponseDTO(foundSchedule, "Schedule successfully retrieved");
//    }



    @Override
    public  Schedule findSchedulesById(Long scheduleId) {
        log.info("Fetching schedule with ID: {}", scheduleId);
        Schedule foundSchedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ScheduleCannotBeFoundException("Schedule not found"));
        log.error("Schedule not found for ID: {}", scheduleId);
        return foundSchedule;
    }

    public ScheduleResponse findSchedule(ScheduleDTO findScheduleDTO) {
        Station arrivalStation = stationService.findStationById(findScheduleDTO.getArrivalStationId());
        Station departureStation = stationService.findStationById(findScheduleDTO.getDepartureStationId());

        if (arrivalStation == null || departureStation== null) {
            throw new ScheduleCannotBeFoundException("Invalid station names provided.");
        }

        List<ScheduleDetailsDTO> schedules = scheduleRepository.findScheduleDetailsByParams(
                departureStation.getStationId(),
                arrivalStation.getStationId(),
                findScheduleDTO.getDepartureDate()
        );

        if (schedules.isEmpty()) {
            throw new ScheduleCannotBeFoundException("No schedules found for the given criteria.");
        }

        return scheduleResponses(schedules);
    }




    private ScheduleResponse scheduleResponses(List<ScheduleDetailsDTO> schedules) {
        return new ScheduleResponse(schedules);
    }







}