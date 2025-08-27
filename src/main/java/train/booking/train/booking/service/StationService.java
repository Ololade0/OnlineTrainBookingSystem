package train.booking.train.booking.service;

import org.springframework.data.domain.Page;
import train.booking.train.booking.dto.StationDto;
import train.booking.train.booking.dto.response.BaseResponse;
import train.booking.train.booking.model.Station;
import train.booking.train.booking.model.User;

import java.util.Optional;


public interface StationService {
    BaseResponse createNewStation(StationDto stationDto);

    Optional<Station> findStationByName(String stationName);

   Station findStationById(Long stationId);

    BaseResponse updateStation(Long stationId, StationDto stationDto);
    BaseResponse deleteStation(Long stationId);


    Page<Station> getAllstations(int page, int size);

    Page<Station> searchStation(String query, int page, int size);

    String getStationNameById(Long arrivalStationId);
}
