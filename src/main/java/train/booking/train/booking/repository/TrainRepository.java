package train.booking.train.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import train.booking.train.booking.model.Train;

@Repository
public interface TrainRepository extends JpaRepository<Train, Long> {
}
