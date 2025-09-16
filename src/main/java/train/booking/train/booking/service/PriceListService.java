package train.booking.train.booking.service;

import train.booking.train.booking.dto.PriceListDTO;
import train.booking.train.booking.dto.UpdatePriceDTO;
import train.booking.train.booking.model.PriceList;
import train.booking.train.booking.model.enums.AgeRange;
import train.booking.train.booking.model.enums.TrainClass;

import java.util.List;

public interface PriceListService {
//   List<PriceList> createPrice(List<PriceListDTO> price, Long scheduleId);
    List<PriceList> createPrices(List<PriceListDTO> priceDtos, Long scheduleId);

    List<PriceListDTO> getPricesByScheduleId(Long id);

    PriceList getSpecificPrice(Long scheduleId, TrainClass trainClass, AgeRange ageRange);
//   List<PriceList> getPriceListByScheduleAndStation(Long scheduleId, Long stationId);

//   PriceList getSpecificPrice(Long scheduleId, Long stationId, TrainClass trainClass, AgeRange ageRange);


//   PriceList updatePriceList(Long priceId, UpdatePriceDTO updatePriceDTO);
}
