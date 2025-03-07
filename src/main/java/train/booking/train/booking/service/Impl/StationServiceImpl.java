package train.booking.train.booking.service.Impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import train.booking.train.booking.exceptions.StationAlreadyExistException;
import train.booking.train.booking.exceptions.StationCannotBeFoundException;
import train.booking.train.booking.model.Station;
import train.booking.train.booking.repository.StationRepository;
import train.booking.train.booking.service.StationService;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class StationServiceImpl implements StationService {

    private final StationRepository stationRepository;

    @Override
    public Station createNewStation(Station newStation) {
        verifyStation(newStation.getStationName(), newStation.getStationCode());
        Station station = Station.builder()
                .stationCode(newStation.getStationCode())
                .stationName(newStation.getStationName())
                .build();
        return stationRepository.save(station);
    }

    @Override
    public Station findStation(String station) {
        return null;
    }

    @Override
    public Station findStationById(Long stationId) {
        return stationRepository.findById(stationId)
                .orElseThrow(() -> new StationCannotBeFoundException("Station with ID " + stationId + " cannot be found"));
    }

    @Override
    public Optional<Station> findStationByName(String stationName) {
        return Optional.ofNullable(stationRepository.findByStationName(stationName).orElseThrow(()
                -> new StationCannotBeFoundException
                ("Station with Name " + stationName + " cannot be found")));
    }



    @Transactional
    public Station updateStation(Station station, Long stationId) {
        Optional<Station> foundStation = stationRepository.findById(stationId);
        if (foundStation.isPresent()) {
            Station existingStation = foundStation.get();
            existingStation.setStationCode(station.getStationCode());
            existingStation.setStationName(station.getStationName());
            return stationRepository.save(existingStation);
        }
        throw new StationCannotBeFoundException("Station with ID " + stationId + " cannot be found");
    }

    @Override
    public List<Station> findAllStation() {
        return stationRepository.findAll();
    }


    private void verifyStation(String stationCode, String stationName) {
        if (stationRepository.existsByStationCode(stationCode)) {
            throw new StationAlreadyExistException("Station with code " + stationCode + " already exists");
        }
        if (stationRepository.existsByStationName(stationName)) {
            throw new StationAlreadyExistException("Station with name " + stationName + " already exists");
        }



    }
}
