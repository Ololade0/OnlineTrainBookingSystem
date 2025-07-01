package train.booking.train.booking.service;

import train.booking.train.booking.dto.StationDto;
import train.booking.train.booking.dto.response.BaseResponse;
import train.booking.train.booking.model.Station;

import java.util.Optional;


public interface StationService {
    BaseResponse createNewStation(StationDto stationDto);

    Optional<Station> findStationByName(String stationName);

   Station findStationById(Long stationId);


}
