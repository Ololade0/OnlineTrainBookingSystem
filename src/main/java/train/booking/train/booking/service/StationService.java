package train.booking.train.booking.service;

import train.booking.train.booking.model.Station;

import java.util.List;
import java.util.Optional;
public interface StationService {
    Station createNewStation(Station newStation);
    Station findStation(String station);
    Station findStationById(Long stationId);
    Optional<Station> findStationByName(String stationName);
    Station updateStation(Station station, Long stationId);

     List<Station> findAllStation();
}
