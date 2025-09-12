package train.booking.train.booking.service.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import train.booking.train.booking.dto.StationDto;
import train.booking.train.booking.dto.response.BaseResponse;
import train.booking.train.booking.dto.response.ResponseUtil;
import train.booking.train.booking.exceptions.StationCannotBeFoundException;
import train.booking.train.booking.model.Station;
import train.booking.train.booking.model.User;
import train.booking.train.booking.repository.StationRepository;
import train.booking.train.booking.service.StationService;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class StationServiceImpl implements StationService {

    private final StationRepository stationRepository;

    @Override
    public BaseResponse createNewStation(StationDto stationDto) {
        verifyStation(stationDto.getStationCode(), stationDto.getStationName());
        Station station = Station.builder()
                .stationCode(stationDto.getStationCode())
                .stationName(stationDto.getStationName())
                .build();
         stationRepository.save(station);
         StationDto responseDto = StationDto
                 .builder()
                 .stationName(station.getStationName())
                 .build();
        return ResponseUtil.success("Station sucessfully created", responseDto);
    }



    private BaseResponse verifyStation(String stationCode, String stationName) {
        if (stationRepository.existsByStationCode(stationCode)) {
            return ResponseUtil.inputAlreadyExist("Station with code '" + stationCode + "' already exists.");
        }
        if (stationRepository.existsByStationName(stationName)) {
            return ResponseUtil.inputAlreadyExist("Station with name '" + stationName + "' already exists.");
        }

        return ResponseUtil.success("Station verification passed", null);
    }

    @Override
    public Optional<Station> findStationByName(String stationName) {
      Optional<Station> foundStation =  Optional.ofNullable(stationRepository.findByStationName(stationName).orElseThrow(()
                -> new StationCannotBeFoundException
                ("Station with Name " + stationName + " cannot be found")));
        return foundStation;
    }

    @Override
    public  Station findStationById(Long stationId) {
        return stationRepository.findById(stationId)
                .orElseThrow(() -> new StationCannotBeFoundException("Station not found"));
    }

    @Override
    public BaseResponse updateStation(Long stationId, StationDto stationDto) {
        Station station = stationRepository.findById(stationId)
                .orElseThrow(() -> new StationCannotBeFoundException("Station not found with id: " + stationId));

        station.setStationName(stationDto.getStationName());
        station.setStationCode(stationDto.getStationCode());

        stationRepository.save(station);
        return ResponseUtil.success("Station updated successfully", station);
    }

    @Override
    public BaseResponse deleteStation(Long stationId) {
        Station station = stationRepository.findById(stationId)
                .orElseThrow(() -> new StationCannotBeFoundException("Station not found with id: " + stationId));

        stationRepository.delete(station);
        return ResponseUtil.success("Station deleted successfully", true);
    }

    @Override
    public Page<Station> getAllstations(int page, int size) {
        return stationRepository.findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));
    }

    @Override
    public Page<Station> searchStation(String query, int page, int size) {
        return stationRepository.searchByNameOrCode(query,PageRequest.of(page, size));
    }

    @Override
    public String getStationNameById(Long id) {
        return stationRepository.findById(id)
                .map(Station::getStationName)
                .orElse("Unknown Station");
    }
}

