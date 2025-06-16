package train.booking.train.booking.service;

import train.booking.train.booking.dto.PriceListDTO;
import train.booking.train.booking.dto.UpdatePriceDTO;
import train.booking.train.booking.model.PriceList;
import train.booking.train.booking.model.enums.TrainClass;

import java.util.List;

public interface PriceListService {
   List<PriceList> createPrice(List<PriceListDTO> price, Long scheduleId);
   List<PriceList> getPriceListByScheduleAndStation(Long scheduleId, Long stationId);

   PriceList getSpecificPrice(Long scheduleId, Long stationId, TrainClass trainClass, String ageRange);


   PriceList updatePriceList(Long priceId, UpdatePriceDTO updatePriceDTO);
}
