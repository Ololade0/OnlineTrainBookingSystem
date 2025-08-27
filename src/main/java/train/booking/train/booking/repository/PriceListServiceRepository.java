package train.booking.train.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import train.booking.train.booking.model.PriceList;
import train.booking.train.booking.model.enums.AgeRange;
import train.booking.train.booking.model.enums.TrainClass;

import java.util.List;
import java.util.Optional;

@Repository

public interface PriceListServiceRepository extends JpaRepository<PriceList, Long> {
    boolean existsByTrainClassAndScheduleIdAndStationId(TrainClass trainClass, Long scheduleId, Long stationId);

    boolean existsByTrainClassAndAgeRangeAndScheduleIdAndStationId(TrainClass trainClass, AgeRange ageRange, Long scheduleId, Long stationId);

    boolean existsByTrainClassAndScheduleIdAndStationIdAndAgeRange(TrainClass trainClass, Long scheduleId, Long stationId, AgeRange ageRange);

//    List<PriceList> findByScheduleIdAndStationId(Long scheduleId, Long stationId);

//    Optional<PriceList> findByScheduleIdAndStationIdAndTrainClassAndAgeRange(
//            Long scheduleId, Long stationId, TrainClass trainClass, AgeRange ageRange
//    );

    boolean existsByTrainClassAndScheduleIdAndAgeRange(TrainClass trainClass, Long scheduleId, AgeRange ageRange);

    List<PriceList> findAllByScheduleId(Long scheduleId);
}
