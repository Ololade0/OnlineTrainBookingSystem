package train.booking.train.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import train.booking.train.booking.dto.PriceListDTO;
import train.booking.train.booking.model.PriceList;
import train.booking.train.booking.service.PriceListService;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/v1/auth/price")
@RequiredArgsConstructor
public class PriceListController {

    private final PriceListService priceListService;

    @PostMapping("create-price/{scheduleId}/{stationId}")
    public ResponseEntity<?> createPrice(@RequestBody List<PriceListDTO> priceListDTO, @PathVariable Long scheduleId, @PathVariable Long stationId) {
       List<PriceList> response =  priceListService.createPrice(priceListDTO, scheduleId, stationId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}
