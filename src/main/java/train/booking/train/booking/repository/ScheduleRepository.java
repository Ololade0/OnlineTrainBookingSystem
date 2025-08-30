package train.booking.train.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import train.booking.train.booking.dto.ScheduleDetailsDTO;
import train.booking.train.booking.dto.ScheduleResponseDTO;
import train.booking.train.booking.model.Schedule;
import train.booking.train.booking.model.enums.Route;
import train.booking.train.booking.model.enums.ScheduleType;

import java.time.LocalDate;
import java.time.LocalTime;
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
                price_list p ON s.id = p.schedule_id
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


    List<Schedule> findByRoute(Route route);


    @Query("SELECT s FROM schedules s WHERE " +
            "(:scheduleType IS NULL OR s.scheduleType = :scheduleType) AND " +
            "(:route IS NULL OR s.route = :route) AND " +
            "(:departureDate IS NULL OR s.departureDate >= :departureDate) AND " +
            "(:arrivalDate IS NULL OR s.arrivalDate <= :arrivalDate) AND " +
            "(:departureTime IS NULL OR s.departureTime >= :departureTime) AND " +
            "(:arrivalTime IS NULL OR s.arrivalTime <= :arrivalTime)")
    Page<Schedule> findSchedulesByCriteria(
            @Param("scheduleType") ScheduleType scheduleType,
            @Param("route") Route route,
            @Param("departureDate") LocalDate departureDate,
            @Param("arrivalDate") LocalDate arrivalDate,
            @Param("departureTime") LocalTime departureTime,
            @Param("arrivalTime") LocalTime arrivalTime,
            Pageable pageable
    );

}


