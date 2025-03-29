package train.booking.train.booking.service;

import train.booking.train.booking.dto.StationDto;
import train.booking.train.booking.dto.response.BaseResponse;
import train.booking.train.booking.model.Station;


public interface StationService {
    BaseResponse createNewStation(StationDto stationDto);

}
