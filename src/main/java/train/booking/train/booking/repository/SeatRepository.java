package train.booking.train.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import train.booking.train.booking.model.Seat;
import train.booking.train.booking.model.enums.TrainClass;

import java.util.List;
import java.util.Optional;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {
    Optional<Seat> findBySeatNumber(int seatNumber);
    Seat findBySeatNumberAndTrainClass(int seatNumber, TrainClass trainClass);
;

}


