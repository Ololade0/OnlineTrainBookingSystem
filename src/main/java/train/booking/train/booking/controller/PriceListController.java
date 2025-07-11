package train.booking.train.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import train.booking.train.booking.dto.PriceListDTO;
import train.booking.train.booking.dto.UpdatePriceDTO;
import train.booking.train.booking.model.PriceList;
import train.booking.train.booking.model.enums.TrainClass;
import train.booking.train.booking.service.PriceListService;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/v1/auth/price")
@RequiredArgsConstructor
public class PriceListController {

    private final PriceListService priceListService;

    @PostMapping("create-price/{scheduleId}")
    public ResponseEntity<?> createPrice(@RequestBody List<PriceListDTO> priceListDTO, @PathVariable Long scheduleId) {
       List<PriceList> response =  priceListService.createPrice(priceListDTO, scheduleId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    public ResponseEntity<?>updatePrice(@PathVariable Long priceId, UpdatePriceDTO updatePriceDTO){
      PriceList updatedPriceList =   priceListService.updatePriceList(priceId, updatePriceDTO);
      return  ResponseEntity.ok(updatedPriceList);

    }



        @GetMapping("/list")
        public ResponseEntity<List<PriceList>> getPrices(
                @RequestParam Long scheduleId,
                @RequestParam Long stationId
        ) {
            return ResponseEntity.ok(priceListService.getPriceListByScheduleAndStation(scheduleId, stationId));
        }

        @GetMapping("/detail")
        public ResponseEntity<PriceList> getSpecificPrice(
                @RequestParam Long scheduleId,
                @RequestParam Long stationId,
                @RequestParam TrainClass trainClass,
                @RequestParam String ageRange
        ) {
            return ResponseEntity.ok(priceListService.getSpecificPrice(scheduleId, stationId, trainClass, ageRange));
        }
    }



