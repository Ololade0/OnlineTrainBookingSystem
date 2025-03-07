package train.booking.train.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import train.booking.train.booking.model.Seat;

import java.util.Optional;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {


        Optional<Seat> findBySeatNumber(int seatNumber);

        @Query("SELECT s FROM seats s WHERE s.seatNumber = :seatNumber AND s.trainClass.trainClassName = :trainClassName")
        Optional<Seat> findBySeatNumberAndTrainClassName(@Param("trainClassName") String trainClassName, @Param("seatNumber") int seatNumber);

        Optional<Seat> findByTrainClass_TrainClassNameAndSeatNumber(String trainClassName, int seatNumber);
    }


