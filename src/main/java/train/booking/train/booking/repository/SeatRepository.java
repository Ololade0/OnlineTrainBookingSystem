package train.booking.train.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import train.booking.train.booking.model.Seat;
import train.booking.train.booking.model.enums.SeatStatus;
import train.booking.train.booking.model.enums.TrainClass;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {
    Optional<Seat> findBySeatNumber(int seatNumber);
    Seat findBySeatNumberAndTrainClass(int seatNumber, TrainClass trainClass);


    List<Seat> findBySeatStatusAndLockTimeBefore(SeatStatus reserved, LocalDateTime threshold);
    Optional<Seat> findBySeatNumberAndTrainIdAndTrainClass(int seatNumber, Long trainId, TrainClass trainClass);

    @Query("SELECT s.seatNumber FROM seats s WHERE s.trainId = :trainId AND s.trainClass = :trainClass")
    Set<Integer> findSeatNumbersByTrainIdAndTrainClass(@Param("trainId") Long trainId,
                                                       @Param("trainClass") TrainClass trainClass);

    List<Seat> findByTrainId(Long trainId);

    ;

}


