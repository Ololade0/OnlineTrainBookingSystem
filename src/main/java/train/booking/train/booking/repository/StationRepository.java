package train.booking.train.booking.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import train.booking.train.booking.model.Station;

import java.util.Optional;

@Repository
public interface StationRepository extends JpaRepository<Station, Long> {

    Optional<Station> findByStationName(String stationName);


    boolean existsByStationName(String stationName);
    boolean existsByStationCode(String stationCode);

    @Query("SELECT s FROM stations s WHERE LOWER(s.stationName) LIKE LOWER(CONCAT('%', ?1, '%')) " +
            "OR LOWER(s.stationCode) LIKE LOWER(CONCAT('%', ?1, '%'))")
    Page<Station> searchByNameOrCode(String query, Pageable pageable);
}
