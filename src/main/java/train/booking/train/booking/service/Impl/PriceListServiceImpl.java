package train.booking.train.booking.service.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import train.booking.train.booking.dto.PriceListDTO;
import train.booking.train.booking.exceptions.PriceAlreadyExist;
import train.booking.train.booking.exceptions.PriceListException;
import train.booking.train.booking.model.PriceList;
import train.booking.train.booking.repository.PriceListServiceRepository;
import train.booking.train.booking.service.PriceListService;
import train.booking.train.booking.service.TrainService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PriceListServiceImpl implements PriceListService {

    private final PriceListServiceRepository priceListServiceRepository;
    private final TrainService trainService;



//    @Transactional
//    public List<PriceList> createPrice(List<PriceListDTO> priceDtos, Long scheduleId) {
//        if (priceDtos == null || priceDtos.isEmpty()) {
//            throw new PriceListException("Price list cannot be null or empty.");
//        }
////        Train train = trainService.findTrainById(priceDtos.get());
//
//        // Track unique combinations of trainClass + ageRange in the request
//        Set<String> uniqueKeys = new HashSet<>();
//
//        List<PriceList> savedPrices = new ArrayList<>();
//
//        for (PriceListDTO dto : priceDtos) {
//
//            // Validate: price > 0
//            if (dto.getPrice() == null || dto.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
//                throw new IllegalArgumentException("Price must be greater than zero for trainClass "
//                        + dto.getTrainClass() + " and ageRange " + dto.getAgeRange());
//            }
//
//           // Check request duplicates
//            String key = dto.getTrainClass().name() + "-" + dto.getAgeRange().name();
//            if (!uniqueKeys.add(key)) {
//                throw new TrainClassCannotBeFoundException("Duplicate price entry for trainClass "
//                        + dto.getTrainClass() + " and ageRange " + dto.getAgeRange());
//            }
//
//            // Check DB duplicates
//            boolean exists = priceListServiceRepository.existsByTrainClassAndScheduleIdAndAgeRange(
//                    dto.getTrainClass(), scheduleId, dto.getAgeRange()
//            );
//            if (exists) {
//                throw new IllegalArgumentException("Price already exists in DB for trainClass "
//                        + dto.getTrainClass() + " and ageRange " + dto.getAgeRange());
//            }
//
//            // Save new PriceList entry
//            PriceList newPrice = PriceList.builder()
//                    .trainClass(dto.getTrainClass())
//                    .ageRange(dto.getAgeRange())
//                    .price(dto.getPrice())
//                    .scheduleId(scheduleId)
//                    .build();
//
//            savedPrices.add(priceListServiceRepository.save(newPrice));
//        }
//
//        log.info("✅ Created {} price entries for schedule {}", savedPrices.size(), scheduleId);
//        return savedPrices;
//    }



    @Transactional
    public List<PriceList> createPrices(List<PriceListDTO> priceDtos, Long scheduleId) {
        if (priceDtos == null || priceDtos.isEmpty()) {
            throw new PriceListException("Price list cannot be null or empty.");
        }

        // Track duplicates within request
        Set<String> uniqueKeys = new HashSet<>();

        // Load all existing prices for this schedule in one go
        List<PriceList> existingPrices = priceListServiceRepository.findAllByScheduleId(scheduleId);
        Set<String> existingKeys = existingPrices.stream()
                .map(p -> p.getTrainClass().name() + "-" + p.getAgeRange().name())
                .collect(Collectors.toSet());

        List<PriceList> toSave = new ArrayList<>();

        for (PriceListDTO dto : priceDtos) {
            // Validate price > 0
            if (dto.getPrice() == null || dto.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
                throw new PriceListException("Price must be greater than zero for trainClass "
                        + dto.getTrainClass() + " and ageRange " + dto.getAgeRange());
            }

            // Check for duplicates in request
            String key = dto.getTrainClass().name() + "-" + dto.getAgeRange().name();
            if (!uniqueKeys.add(key)) {
                throw new PriceListException("Duplicate entry for trainClass "
                        + dto.getTrainClass() + " and ageRange " + dto.getAgeRange());
            }

            // Check for duplicates in DB
            if (existingKeys.contains(key)) {
                throw new PriceAlreadyExist("Price already exists in DB for trainClass "
                        + dto.getTrainClass() + " and ageRange " + dto.getAgeRange());
            }

            // Build PriceList entry
            PriceList newPrice = PriceList.builder()
                    .trainClass(dto.getTrainClass())
                    .ageRange(dto.getAgeRange())
                    .price(dto.getPrice())
                    .scheduleId(scheduleId)
                    .build();

            toSave.add(newPrice);
        }

        // ✅ Batch save
        List<PriceList> savedPrices = priceListServiceRepository.saveAll(toSave);
        log.info("✅ Created {} price entries for schedule {}", savedPrices.size(), scheduleId);

        return savedPrices;
    }

    public List<PriceListDTO> getPricesByScheduleId(Long scheduleId) {
        List<PriceList> prices = priceListServiceRepository.findAllByScheduleId(scheduleId);

        return prices.stream()
                .map(price -> PriceListDTO.builder()
                        .id(price.getId())
                        .trainClass(price.getTrainClass())
                        .ageRange(price.getAgeRange())
                        .price(price.getPrice())
                        .scheduleId(price.getScheduleId())
                        .build()
                )
                .toList();
    }
}


//    @Override
//    public List<PriceList> getPriceListByScheduleAndStation(Long scheduleId, Long stationId) {
//        return priceListServiceRepository.findByScheduleIdAndStationId(scheduleId, stationId);
//    }


//    PriceList price = priceListServiceRepository
//            .findByTrainClassAndScheduleIdAndStationIdAndAgeRange(trainClass, scheduleId, stationId, ageRange)
//            .orElseThrow(() -> new RuntimeException("Price not found for this combination"));
//
//    @Override
//
//    public PriceList getSpecificPrice(Long scheduleId, Long stationId, TrainClass trainClass, AgeRange ageRange) {
//        return priceListServiceRepository
//                .findByScheduleIdAndStationIdAndTrainClassAndAgeRange(scheduleId, stationId, trainClass, ageRange)
//                .orElseThrow(() -> new PriceListException("Price not found for given criteria"));
//    }


//    @Override
//    public PriceList updatePriceList(Long priceId, UpdatePriceDTO updatePriceDTO) {
//        Schedule foundSchedule = scheduleService.findSchedulesById(updatePriceDTO.getScheduleId());
//     PriceList foundPriceList =   priceListServiceRepository.findById(priceId).orElseThrow(()
//             -> new PriceListException("PriceList cannot be found"));
//     if(foundPriceList != null) {
//         if (foundSchedule != null) {
//             foundPriceList.setPrice(updatePriceDTO.getPrice());
//             foundPriceList.setTrainClass(updatePriceDTO.getTrainClass());
//             foundPriceList.setAgeRange(updatePriceDTO.getAgeRange());
//             return priceListServiceRepository.save(foundPriceList);
//
//         }
//         throw new PriceListException("PriceList cannot be found");
//     }
//       throw new PriceListException("PriceList cannot be found");
//     }
//    }


