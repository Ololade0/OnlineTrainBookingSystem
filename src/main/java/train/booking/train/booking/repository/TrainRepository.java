package train.booking.train.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import train.booking.train.booking.model.Train;
import train.booking.train.booking.model.enums.TrainClass;

import java.util.Set;

@Repository
public interface TrainRepository extends JpaRepository<Train, Long> {
    boolean existsByTrainName(String trainName);

    boolean existsByTrainCode(String trainCode);

    boolean existsByTrainClasses(Set<TrainClass> trainClasses);
}
