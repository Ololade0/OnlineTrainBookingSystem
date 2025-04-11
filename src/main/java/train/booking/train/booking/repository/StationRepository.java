package train.booking.train.booking.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import train.booking.train.booking.model.Station;

import java.util.Optional;

@Repository
public interface StationRepository extends JpaRepository<Station, Long> {

    Optional<Station> findByStationName(String stationName);


    boolean existsByStationName(String stationName);
    boolean existsByStationCode(String stationCode);
}
