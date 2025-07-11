package train.booking.train.booking.service.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import train.booking.train.booking.dto.PriceListDTO;
import train.booking.train.booking.dto.UpdatePriceDTO;
import train.booking.train.booking.exceptions.PriceAlreadyExist;
import train.booking.train.booking.exceptions.PriceListException;
import train.booking.train.booking.model.PriceList;
import train.booking.train.booking.model.Schedule;
import train.booking.train.booking.model.enums.TrainClass;
import train.booking.train.booking.repository.PriceListServiceRepository;
import train.booking.train.booking.service.PriceListService;
import train.booking.train.booking.service.ScheduleService;
import train.booking.train.booking.service.StationService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PriceListServiceImpl implements PriceListService {

    private final PriceListServiceRepository priceListServiceRepository;
    private final ScheduleService scheduleService;
    private final StationService stationService;

    @Override
    public List<PriceList> createPrice (List<PriceListDTO> price, Long scheduleId) {
        Schedule schedule = scheduleService.findSchedulesById(scheduleId);

        // Track unique combinations of trainClass and ageRange within the request
        Set<String> uniqueTrainClassAgeRange = new HashSet<>();

        for (PriceListDTO dto : price) {
            String uniqueKey = dto.getTrainClass().name() + "-" + dto.getAgeRange();

            if (!uniqueTrainClassAgeRange.add(uniqueKey)) {
                throw new PriceAlreadyExist("Duplicate combination of trainClass and ageRange found: " + uniqueKey);
            }
        }

        // Check if a price already exists in the DB for that trainClass + schedule + ageRange
        for (PriceListDTO dto : price) {
            boolean exists = priceListServiceRepository.existsByTrainClassAndScheduleIdAndAgeRange(
                    dto.getTrainClass(), scheduleId, dto.getAgeRange()
            );
            if (exists) {
                throw new IllegalArgumentException("Price for trainClass '" + dto.getTrainClass() +
                        "' and ageRange '" + dto.getAgeRange() + "' already exists for this schedule.");
            }
        }

        // Save the new prices
        List<PriceList> savedPrice = new ArrayList<>();
        for (PriceListDTO dto : price) {
            PriceList newPrice = new PriceList();
            newPrice.setTrainClass(dto.getTrainClass());
            newPrice.setAgeRange(dto.getAgeRange());
            newPrice.setPrice(dto.getPrice());
            newPrice.setScheduleId(schedule.getId());
            savedPrice.add(priceListServiceRepository.save(newPrice));
        }

        return savedPrice;
    }


    @Override
    public List<PriceList> getPriceListByScheduleAndStation(Long scheduleId, Long stationId) {
        return priceListServiceRepository.findByScheduleIdAndStationId(scheduleId, stationId);
    }


//    PriceList price = priceListServiceRepository
//            .findByTrainClassAndScheduleIdAndStationIdAndAgeRange(trainClass, scheduleId, stationId, ageRange)
//            .orElseThrow(() -> new RuntimeException("Price not found for this combination"));

    @Override

    public PriceList getSpecificPrice(Long scheduleId, Long stationId, TrainClass trainClass, String ageRange) {
        return priceListServiceRepository
                .findByScheduleIdAndStationIdAndTrainClassAndAgeRange(scheduleId, stationId, trainClass, ageRange)
                .orElseThrow(() -> new PriceListException("Price not found for given criteria"));
    }


    @Override
    public PriceList updatePriceList(Long priceId, UpdatePriceDTO updatePriceDTO) {
        Schedule foundSchedule = scheduleService.findSchedulesById(updatePriceDTO.getScheduleId());
     PriceList foundPriceList =   priceListServiceRepository.findById(priceId).orElseThrow(()
             -> new PriceListException("PriceList cannot be found"));
     if(foundPriceList != null) {
         if (foundSchedule != null) {
             foundPriceList.setPrice(updatePriceDTO.getPrice());
             foundPriceList.setTrainClass(updatePriceDTO.getTrainClass());
             foundPriceList.setAgeRange(updatePriceDTO.getAgeRange());
             return priceListServiceRepository.save(foundPriceList);

         }
         throw new PriceListException("PriceList cannot be found");
     }
       throw new PriceListException("PriceList cannot be found");
     }
    }


