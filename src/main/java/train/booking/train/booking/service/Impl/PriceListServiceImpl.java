package train.booking.train.booking.service.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import train.booking.train.booking.dto.PriceListDTO;

import train.booking.train.booking.dto.response.BaseResponse;
import train.booking.train.booking.dto.response.ResponseUtil;
import train.booking.train.booking.model.*;
import train.booking.train.booking.model.enums.AgeRange;

import train.booking.train.booking.repository.PriceListServiceRepository;
import train.booking.train.booking.service.PriceListService;
import train.booking.train.booking.service.ScheduleService;
import train.booking.train.booking.service.StationService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PriceListServiceImpl implements PriceListService {

    private final PriceListServiceRepository priceListServiceRepository;
    private final ScheduleService scheduleService;
    private final StationService stationService;


    public List<PriceList> createPrice(List<PriceListDTO> price, Long scheduleId, Long stationId) {
        Schedule schedule = scheduleService.findSchedulesById(scheduleId);
        Station station = stationService.findStationById(stationId);
        List<PriceList> savedPrice = new ArrayList<>();
        for (PriceListDTO priceListDTO : price) {
            PriceList newPrice = new PriceList();
            newPrice.setTrainClass(priceListDTO.getTrainClass());
            newPrice.setAgeRange(priceListDTO.getAgeRange());
            newPrice.setPrice(priceListDTO.getPrice());
            newPrice.setStationId(station.getStationId());
            newPrice.setScheduleId(schedule.getId());
            savedPrice.add(priceListServiceRepository.save(newPrice));
        }
        return savedPrice;


    }

}

