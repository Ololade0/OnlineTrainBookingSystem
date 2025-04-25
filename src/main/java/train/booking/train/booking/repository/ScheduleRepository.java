package train.booking.train.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import train.booking.train.booking.dto.ScheduleDTO;
import train.booking.train.booking.dto.ScheduleDetailsDTO;
import train.booking.train.booking.dto.response.BaseResponse;
import train.booking.train.booking.model.Schedule;
import train.booking.train.booking.model.Station;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {



    @Query(value = """
SELECT
    s.id AS scheduleId,
    s.train_id AS trainId,
    s.departure_time AS departureTime,
    s.arrival_time AS arrivalTime,
    s.departure_date AS departureDate,
    s.arrival_date AS arrivalDate,
    s.duration AS duration,
    s.distance AS distance,
    s.schedule_type AS scheduleType,
    s.route AS route,
    dep.station_id AS departureStation,  
    arr.station_id AS arrivalStation,   
    p.train_class AS trainClass,
    p.price AS price,
    p.age_range AS ageRange
FROM 
    schedules s
JOIN 
    stations dep ON s.departure_station_id = dep.station_id
JOIN 
    stations arr ON s.arrival_station_id = arr.station_id
LEFT JOIN 
    price_list p ON s.id = p.schedule_id AND p.station_id = :departureStationId
WHERE 
    s.departure_station_id = :departureStationId  
    AND s.arrival_station_id = :arrivalStationId  
    AND s.departure_date = :travelDate
""", nativeQuery = true)
    List<ScheduleDetailsDTO> findScheduleDetailsByParams(
            @Param("departureStationId") Long departureStationId,
            @Param("arrivalStationId") Long arrivalStationId,
            @Param("travelDate") LocalDate travelDate
    );



}



