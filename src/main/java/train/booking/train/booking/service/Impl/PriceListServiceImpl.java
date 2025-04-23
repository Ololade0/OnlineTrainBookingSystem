package train.booking.train.booking.service.Impl;

import jakarta.mail.search.SearchTerm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import train.booking.train.booking.dto.PriceListDTO;

import train.booking.train.booking.dto.response.BaseResponse;
import train.booking.train.booking.dto.response.ResponseUtil;
import train.booking.train.booking.exceptions.PriceAlreadyExist;
import train.booking.train.booking.model.*;
import train.booking.train.booking.model.enums.AgeRange;

import train.booking.train.booking.repository.PriceListServiceRepository;
import train.booking.train.booking.service.PriceListService;
import train.booking.train.booking.service.ScheduleService;
import train.booking.train.booking.service.StationService;

import java.math.BigDecimal;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PriceListServiceImpl implements PriceListService {

    private final PriceListServiceRepository priceListServiceRepository;
    private final ScheduleService scheduleService;
    private final StationService stationService;


    public List<PriceList> createPrice(List<PriceListDTO> price, Long scheduleId, Long stationId) {
        Schedule schedule = scheduleService.findSchedulesById(scheduleId);
        Station station = stationService.findStationById(stationId);

        // To track unique combinations of trainClass and ageRange
        Set<String> uniqueTrainClassAgeRange = new HashSet<>();

        for (PriceListDTO dto : price) {
            String uniqueKey = dto.getTrainClass().name() + "-" + dto.getAgeRange();

            // Check for duplicates in the request for the same TrainClass and AgeRange
            if (!uniqueTrainClassAgeRange.add(uniqueKey)) {
                throw new PriceAlreadyExist("Duplicate combination of trainClass and ageRange found: " + uniqueKey);
            }
        }

//          Check if a price already exists in the DB for that trainClass + schedule + station + ageRange
        for (PriceListDTO dto : price) {
            boolean exists = priceListServiceRepository.existsByTrainClassAndScheduleIdAndStationIdAndAgeRange(
                    dto.getTrainClass(), scheduleId, stationId, dto.getAgeRange()
            );
            if (exists) {
                throw new IllegalArgumentException("Price for trainClass '" + dto.getTrainClass() +
                        "' and ageRange '" + dto.getAgeRange() + "' already exists for this schedule and station.");
            }
        }

        // Step 3: Save the new prices
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

