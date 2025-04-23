package train.booking.train.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import train.booking.train.booking.model.PriceList;
import train.booking.train.booking.model.enums.TrainClass;
import train.booking.train.booking.model.enums.AgeRange;

@Repository

public interface PriceListServiceRepository extends JpaRepository<PriceList, Long> {
    boolean existsByTrainClassAndScheduleIdAndStationId(TrainClass trainClass, Long scheduleId, Long stationId);

    boolean existsByTrainClassAndAgeRangeAndScheduleIdAndStationId(TrainClass trainClass, AgeRange ageRange, Long scheduleId, Long stationId);

    boolean existsByTrainClassAndScheduleIdAndStationIdAndAgeRange(TrainClass trainClass, Long scheduleId, Long stationId, AgeRange ageRange);
}
