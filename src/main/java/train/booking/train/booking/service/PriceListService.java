package train.booking.train.booking.service;

import org.springframework.transaction.annotation.Transactional;
import train.booking.train.booking.dto.PriceListDTO;
import train.booking.train.booking.dto.response.BaseResponse;
import train.booking.train.booking.model.PriceList;
import train.booking.train.booking.model.Train;
import train.booking.train.booking.model.TrainClass;
import train.booking.train.booking.model.enums.AgeRange;

import java.math.BigDecimal;
import java.util.List;

public interface PriceListService {
   List<PriceList> createPrice(List<PriceListDTO> price, Long scheduleId, Long stationId);

//   PriceList createPrice(TrainClass trainClass, AgeRange ageRange, BigDecimal price, Long scheduleId, Long stationId);
}
