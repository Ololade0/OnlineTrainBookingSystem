package train.booking.train.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import train.booking.train.booking.dto.response.BaseResponse;
import train.booking.train.booking.model.Schedule;
import train.booking.train.booking.model.Station;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    @Query("SELECT s FROM schedules s " + // Use the entity name 'schedules'
            "JOIN s.stations departureStation " +
            "JOIN s.stations arrivalStation " +
            "WHERE departureStation.stationName = :departureStationName " +
            "AND arrivalStation.stationName = :arrivalStationName " +
            "AND s.departureDate = :departureDate")
    List<Schedule> findSchedulesByDepartureAndArrivalStationAndDate(
            @Param("departureStationName") String departureStationName,
            @Param("arrivalStationName") String arrivalStationName,
            @Param("departureDate") LocalDate departureDate
    );
}

//    Schedule findSchedules(Station arrivalStation, Station departureStation, Date date);

