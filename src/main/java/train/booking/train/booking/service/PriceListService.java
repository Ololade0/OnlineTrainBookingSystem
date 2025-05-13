package train.booking.train.booking.service;

import org.springframework.transaction.annotation.Transactional;
import train.booking.train.booking.dto.PriceListDTO;

import train.booking.train.booking.dto.UpdatePriceDTO;
import train.booking.train.booking.model.PriceList;


import java.math.BigDecimal;
import java.util.List;

public interface PriceListService {
   List<PriceList> createPrice(List<PriceListDTO> price, Long scheduleId, Long stationId);


   PriceList updatePriceList(Long priceId, UpdatePriceDTO updatePriceDTO);
}
