package train.booking.train.booking.service.Impl;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import train.booking.train.booking.dto.*;
import train.booking.train.booking.dto.response.BaseResponse;
import train.booking.train.booking.dto.response.ResponseUtil;
import train.booking.train.booking.dto.response.ScheduleResponse;
import train.booking.train.booking.exceptions.ScheduleCannotBeFoundException;
import train.booking.train.booking.exceptions.ScheduleDetailsException;
import train.booking.train.booking.model.Schedule;
import train.booking.train.booking.model.Station;
import train.booking.train.booking.model.Train;
import train.booking.train.booking.model.enums.Route;
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

    @Override
    public  Schedule findSchedulesById(Long scheduleId) {
        log.info("Fetching schedule with ID: {}", scheduleId);
        Schedule foundSchedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ScheduleCannotBeFoundException("Schedule not found"));
        log.error("Schedule not found for ID: {}", scheduleId);
        return foundSchedule;
    }


    public ScheduleResponse findSchedule(Long departureId,
                                         Long arrivalStationId,
                                         LocalDate departureDate) {
        Station arrivalStation = stationService.findStationById(arrivalStationId);
        Station departureStation = stationService.findStationById(departureId);

        if (arrivalStation == null || departureStation == null) {
            throw new ScheduleCannotBeFoundException("Invalid station names provided.");
        }

        List<ScheduleDetailsDTO> scheduleDetails = scheduleRepository.findScheduleDetailsByParams(
                departureStation.getStationId(),
                arrivalStation.getStationId(),
               departureDate
        );

        if (scheduleDetails.isEmpty()) {
            throw new ScheduleCannotBeFoundException("No schedules found for the given criteria.");
        }

        // Group by scheduleId
        Map<Long, ScheduleDTO> groupedSchedules = groupBySchedule_IdAndPrice(scheduleDetails);
        return scheduleResponses(new ArrayList<>(groupedSchedules.values()));
    }

    private static Map<Long, ScheduleDTO> groupBySchedule_IdAndPrice(List<ScheduleDetailsDTO> flatList) {
        Map<Long, ScheduleDTO> groupedSchedules = new LinkedHashMap<>();

        for (ScheduleDetailsDTO dto : flatList) {
            ScheduleDTO grouped = groupedSchedules.get(dto.getScheduleId());

            if (grouped == null) {
                grouped = new ScheduleDTO();
                grouped.setScheduleId(dto.getScheduleId());
                grouped.setTrainId(dto.getTrainId());
                grouped.setDepartureStationId(dto.getDepartureStation());
                grouped.setArrivalStationId(dto.getArrivalStation());
                grouped.setRoute(dto.getRoute());
                grouped.setScheduleType(dto.getScheduleType());
                grouped.setDistance(dto.getDistance());
                grouped.setDuration(dto.getDuration());
                grouped.setDepartureDate(dto.getDepartureDate());
                grouped.setArrivalDate(dto.getArrivalDate());
                grouped.setDepartureTime(dto.getDepartureTime());
                grouped.setArrivalTime(dto.getArrivalTime());
                grouped.setPrices(new ArrayList<>());
                groupedSchedules.put(dto.getScheduleId(), grouped);
            }

            if (dto.getTrainClass() != null && dto.getAgeRange() != null && dto.getPrice() != null) {
                grouped.getPrices().add(new PriceListDTO(dto.getTrainClass(), dto.getAgeRange(), dto.getPrice()));
            }
        }
        return groupedSchedules;
    }


    private ScheduleResponse scheduleResponses(List<ScheduleDTO> schedules) {
        return new ScheduleResponse(schedules);
    }









}