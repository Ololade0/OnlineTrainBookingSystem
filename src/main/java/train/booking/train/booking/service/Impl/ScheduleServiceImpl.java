package train.booking.train.booking.service.Impl;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import train.booking.train.booking.dto.ScheduleResponseDTO;
import train.booking.train.booking.dto.PriceListDTO;
import train.booking.train.booking.dto.ScheduleDTO;
import train.booking.train.booking.dto.ScheduleDetailsDTO;
import train.booking.train.booking.dto.response.BaseResponse;
import train.booking.train.booking.dto.response.ResponseUtil;
import train.booking.train.booking.dto.response.ScheduleResponse;
import train.booking.train.booking.exceptions.ScheduleCannotBeFoundException;
import train.booking.train.booking.exceptions.ScheduleDetailsException;
import train.booking.train.booking.model.Schedule;
import train.booking.train.booking.model.Station;
import train.booking.train.booking.model.Train;
import train.booking.train.booking.model.enums.Route;
import train.booking.train.booking.model.enums.ScheduleType;
import train.booking.train.booking.model.enums.TrainClass;
import train.booking.train.booking.repository.ScheduleRepository;
import train.booking.train.booking.service.*;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final DistanceCalculatorService distanceCalculatorService;
    private final TrainService trainService;
    private final StationService stationService;
    private final PriceListService priceListService;
    String durationString;

    @Transactional
    public BaseResponse newSchedule(ScheduleDTO scheduleDto) {
        if (scheduleDto == null) {
            throw new ScheduleDetailsException("ScheduleDto cannot be null.");
        }

        validateScheduleDetails(scheduleDto);

        try {
            Train train = trainService.findTrainById(scheduleDto.getTrainId());
            Station arrivalStation = stationService.findStationById(scheduleDto.getArrivalStationId());
            Station departureStation = stationService.findStationById(scheduleDto.getDepartureStationId());

            if (scheduleDto.getDepartureTime() == null || scheduleDto.getArrivalTime() == null) {
                throw new ScheduleDetailsException("Departure time and arrival time must not be null.");
            }

            Duration duration = Duration.between(scheduleDto.getDepartureTime(), scheduleDto.getArrivalTime());
            String durationString = formatDurationToString(duration);

            Schedule schedule = Schedule.builder()
                    .trainId(train.getId())
                    .departureStationId(departureStation.getStationId())
                    .arrivalStationId(arrivalStation.getStationId())
                    .departureTime(scheduleDto.getDepartureTime())
                    .arrivalTime(scheduleDto.getArrivalTime())
                    .departureDate(scheduleDto.getDepartureDate())
                    .arrivalDate(scheduleDto.getArrivalDate())
                    .duration(durationString)
                    .scheduleType(scheduleDto.getScheduleType())
                    .route(scheduleDto.getRoute())
                    .distance(distanceCalculatorService.calculateDistance())
                    .build();

            Schedule savedSchedule = scheduleRepository.save(schedule);

//             ✅ if prices exist, validate classes against train’s enum set
            if (scheduleDto.getPrices() != null && !scheduleDto.getPrices().isEmpty()) {

                // Get the set of allowed classes from this train
                Set<TrainClass> allowedClasses = train.getTrainClasses();

                for (PriceListDTO price : scheduleDto.getPrices()) {
                    // 🚨 Validate that the price’s class is one of the train’s classes
                    if (!allowedClasses.contains(price.getTrainClass())) {
                        throw new ScheduleDetailsException(
                                "TrainClass " + price.getTrainClass() +
                                        " is not assigned to Train " + train.getTrainName()
                        );
                    }
                }

                // ✅ safe to persist prices
                priceListService.createPrices(scheduleDto.getPrices(), savedSchedule.getId());
            }


            return mapToResponseDTO(savedSchedule, "Schedule successfully created");

        } catch (Exception e) {
            log.error("❌ Error creating schedule: {}", e.getMessage(), e);
            throw new ScheduleDetailsException("Failed to create schedule");
        }
    }


//    @Transactional
//    public BaseResponse newSchedule(ScheduleDTO scheduleDto) {
//        if (scheduleDto == null) {
//            throw new ScheduleDetailsException("ScheduleDto cannot be null.");
//        }
//
//        // ✅ validate all required schedule details
//        validateScheduleDetails(scheduleDto);
//
//        try {
//            // ✅ fetch entities from DB
//            Train train = trainService.findTrainById(scheduleDto.getTrainId());
//            Station arrivalStation = stationService.findStationById(scheduleDto.getArrivalStationId());
//            Station departureStation = stationService.findStationById(scheduleDto.getDepartureStationId());
//
//            // ✅ check mandatory times
//            if (scheduleDto.getDepartureTime() == null || scheduleDto.getArrivalTime() == null) {
//                throw new ScheduleDetailsException("Departure time and arrival time must not be null.");
//            }
//
//            // ✅ compute duration between departure and arrival
//            Duration duration = Duration.between(scheduleDto.getDepartureTime(), scheduleDto.getArrivalTime());
//            String durationString = formatDurationToString(duration);
//
//            // ✅ build Schedule entity
//            Schedule schedule = Schedule.builder()
//                    .trainId(train.getId())
//                    .departureStationId(departureStation.getStationId())
//                    .arrivalStationId(arrivalStation.getStationId())
//                    .departureTime(scheduleDto.getDepartureTime())
//                    .arrivalTime(scheduleDto.getArrivalTime())
//                    .departureDate(scheduleDto.getDepartureDate())
//                    .arrivalDate(scheduleDto.getArrivalDate())
//                    .duration(durationString)
//                    .scheduleType(scheduleDto.getScheduleType())
//                    .route(scheduleDto.getRoute())
//                    .distance(distanceCalculatorService.calculateDistance()) // ← you’ll probably pass params here
//                    .build();
//
//            // ✅ save schedule first
//            Schedule savedSchedule = scheduleRepository.save(schedule);
//
//            // ✅ if prices exist, validate classes against train’s enum set
//            if (scheduleDto.getPrices() != null && !scheduleDto.getPrices().isEmpty()) {
//
//                // Get the set of allowed classes from this train
//                Set<TrainClass> allowedClasses = train.getTrainClasses();
//
//                for (PriceListDTO price : scheduleDto.getPrices()) {
//                    // 🚨 Validate that the price’s class is one of the train’s classes
//                    if (!allowedClasses.contains(price.getTrainClass())) {
//                        throw new ScheduleDetailsException(
//                                "TrainClass " + price.getTrainClass() +
//                                        " is not assigned to Train " + train.getTrainName()
//                        );
//                    }
//                }
//
//                // ✅ safe to persist prices
//                priceListService.createPrices(scheduleDto.getPrices(), savedSchedule.getId());
//            }
//
//            // ✅ return response
//            return mapToResponseDTO(savedSchedule, "Schedule successfully created");
//
//        } catch (Exception e) {
//            log.error("❌ Error creating schedule: {}", e.getMessage(), e);
//            throw new ScheduleDetailsException("Failed to create schedule");
//        }
//    }


    private String formatDurationToString(Duration duration) {
        long hours = duration.toHours();
        long minutes = duration.toMinutes() % 60;

        StringBuilder formattedDuration = new StringBuilder();

        if (hours > 0) {
            formattedDuration.append(hours).append("h ");
        }

        if (minutes > 0) {
            formattedDuration.append(minutes).append("m");
        } else if (hours == 0) {
            formattedDuration.append("0m");
        }

        return formattedDuration.toString();
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
        if (scheduleDTO == null) {
            throw new ScheduleDetailsException("ScheduleDto cannot be null.");
        }

        if (scheduleDTO.getDepartureStationId() == null ||
                scheduleDTO.getArrivalStationId() == null ||
                scheduleDTO.getScheduleType() == null ||
                scheduleDTO.getDepartureDate() == null ||
                scheduleDTO.getDepartureTime() == null ||
                scheduleDTO.getArrivalTime() == null) {

            throw new ScheduleDetailsException("All parameters must be provided and cannot be null.");
        }

        // Ensure arrival time is not before departure time
        if (scheduleDTO.getArrivalTime().isBefore(scheduleDTO.getDepartureTime())) {
            throw new ScheduleDetailsException("Arrival time cannot be before departure time.");
        }

        if (scheduleDTO.getDepartureTime().equals(scheduleDTO.getArrivalTime())) {
            throw new ScheduleDetailsException("Departure and arrival time must be different.");
        }

        // Validate departure and arrival dates
        if (scheduleDTO.getArrivalDate() != null &&
                scheduleDTO.getArrivalDate().isBefore(scheduleDTO.getDepartureDate())) {
            throw new ScheduleDetailsException("Arrival date cannot be before departure date.");
        }

        // Ensure departure and arrival stations are different
        if (scheduleDTO.getDepartureStationId().equals(scheduleDTO.getArrivalStationId())) {
            throw new ScheduleDetailsException("Departure and arrival stations must be different.");
        }

        // Validate routes and dates
        if ((scheduleDTO.getRoute() == Route.IBADAN_LAGOS_AFTERNOON_TRAIN ||
                scheduleDTO.getRoute() == Route.LAGOS_IBADAN_AFTERNOON_TRAIN ||
                scheduleDTO.getRoute() == Route.LAGOS_IBADAN_MORNING_TRAIN ||
                scheduleDTO.getRoute() == Route.IBADAN_LAGOS_MORNING_TRAIN) &&
                !scheduleDTO.getDepartureDate().equals(scheduleDTO.getArrivalDate())) {

            throw new ScheduleDetailsException("Departure date must not be different from arrival date.");
        }
    }
    @Override
    public Schedule findSchedulesById(Long scheduleId) {
        log.info("Fetching schedule with ID: {}", scheduleId);
        return scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> {
                    log.error("Schedule not found for ID: {}", scheduleId);
                    return new ScheduleCannotBeFoundException("Schedule not found");
                });
    }

    @Override
    public Page<ScheduleResponseDTO> findAllSchedules(int page, int size) {
        Page<Schedule> schedules = scheduleRepository.findAll(PageRequest.of(page, size));
        if(schedules.isEmpty()){
            throw new ScheduleCannotBeFoundException("All schedules cannot be found");
        }
        return schedules.map(schedule -> {
            ScheduleResponseDTO dto = new ScheduleResponseDTO();
            dto.setId(schedule.getId());
            dto.setDepartureDate(schedule.getDepartureDate());
            dto.setArrivalDate(schedule.getArrivalDate());
            dto.setDepartureTime(schedule.getDepartureTime());
            dto.setArrivalTime(schedule.getArrivalTime());
            dto.setDuration(schedule.getDuration());
            dto.setDistance(schedule.getDistance());
            dto.setScheduleType(schedule.getScheduleType());
            dto.setRoute(schedule.getRoute());

            // Fetch names from respective repositories
            dto.setTrainName(trainService.getTrainNameById(schedule.getTrainId()));
            dto.setArrivalStationName(stationService.getStationNameById(schedule.getArrivalStationId()));
            dto.setDepartureStationName(stationService.getStationNameById(schedule.getDepartureStationId()));
            return dto;
        });
    }



    @Override
    public ScheduleResponse findSchedule(Long departureId,
                                         Long arrivalStationId,
                                         LocalDate departureDate) {
        try {
            Station arrivalStation   = stationService.findStationById(arrivalStationId);
            Station departureStation = stationService.findStationById(departureId);

            if (arrivalStation == null || departureStation == null) {
                throw new ScheduleCannotBeFoundException("Invalid station names provided.");
            }

            List<ScheduleDetailsDTO> scheduleDetails = scheduleRepository
                    .findScheduleDetailsByParams(
                            departureStation.getStationId(),
                            arrivalStation.getStationId(),
                            departureDate
                    );

            if (scheduleDetails.isEmpty()) {
                throw new ScheduleCannotBeFoundException("No schedules found for the given criteria.");
            }

            // Group flat details into per-schedule DTOs
            Map<Long, ScheduleDTO> groupedSchedules = groupBySchedule_IdAndPrice(scheduleDetails);

            // Extract the first schedule DTO and its ID
            List<ScheduleDTO> scheduleList = new ArrayList<>(groupedSchedules.values());
            Long scheduleId = scheduleList.get(0).getScheduleId();

            return scheduleResponses(scheduleId, scheduleList);

        } catch (ScheduleCannotBeFoundException e) {
            // Propagate not-found exceptions as is
            throw e;
        } catch (Exception e) {
            log.error("Error fetching schedule", e);
            throw new ScheduleDetailsException("Error fetching schedule details");
        }
    }


    // Helper to construct the response DTO
    private ScheduleResponse scheduleResponses(Long id, List<ScheduleDTO> schedules) {
        return new ScheduleResponse(id, schedules);
    }

    // Groups a flat list of ScheduleDetailsDTO into ScheduleDTOs keyed by scheduleId
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
                grouped.getPrices()
                        .add(new PriceListDTO(dto.getTrainClass(), dto.getAgeRange(), dto.getPrice()));
            }
        }

        return groupedSchedules;
    }


    @Override
    public BaseResponse updateSchedule(Long scheduleId, ScheduleDTO scheduleDTO) {
        Schedule existingSchedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ScheduleCannotBeFoundException("Schedule not found with ID: " + scheduleId));

        // Validate station and train IDs
        Station departureStation = stationService.findStationById(scheduleDTO.getDepartureStationId());


        Station arrivalStation = stationService.findStationById(scheduleDTO.getArrivalStationId());

        Train train = trainService.findTrainById(scheduleDTO.getTrainId());

        // Update schedule details
        existingSchedule.setDepartureStationId(departureStation.getStationId());
        existingSchedule.setArrivalStationId(arrivalStation.getStationId());
        existingSchedule.setTrainId(train.getId());
        existingSchedule.setDepartureDate(scheduleDTO.getDepartureDate());
        existingSchedule.setDepartureTime(scheduleDTO.getDepartureTime());
        existingSchedule.setArrivalDate(scheduleDTO.getArrivalDate());
        existingSchedule.setArrivalTime(scheduleDTO.getArrivalTime());
        scheduleRepository.save(existingSchedule);

        log.info("Schedule updated successfully: ID {}", scheduleId);
        return ResponseUtil.success("Schedule updated successfully", null);
    }

    @Override
    public BaseResponse deleteSchedule(Long scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ScheduleCannotBeFoundException("Schedule not found with ID: " + scheduleId));
        scheduleRepository.delete(schedule);

        log.info("Schedule deleted: ID {}", scheduleId);
        return ResponseUtil.success("Schedule deleted successfully", null);
    }

    @Override
    public List<Schedule> findByRouteName(Route route) {
        List<Schedule> foundSchedule =  scheduleRepository.findByRoute(route);
        if(foundSchedule.isEmpty()){
            throw new ScheduleCannotBeFoundException("Schedule with this route cannot be found");
        }
        return foundSchedule;

    }

    @Override
    public List<ScheduleType> getScheduleType() {
       List<ScheduleType> scheduleTypeList = Arrays.asList(ScheduleType.values());
       if(scheduleTypeList.isEmpty()){
           throw new ScheduleDetailsException("Schedule Type cannot be found");
       }
       return scheduleTypeList;
    }


    public Page<Schedule> searchSchedules(
            ScheduleType scheduleType,
            Route route,
            LocalDate departureDate,
            LocalDate arrivalDate,
            LocalTime departureTime,
            LocalTime arrivalTime,
            Pageable pageable
    ) {
        return scheduleRepository.findSchedulesByCriteria(
                scheduleType, route, departureDate, arrivalDate, departureTime, arrivalTime, pageable
        );
    }




}



