package train.booking.train.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import train.booking.train.booking.dto.StationDto;
import train.booking.train.booking.dto.response.BaseResponse;
import train.booking.train.booking.service.StationService;

@RestController
@Slf4j
@RequestMapping("/api/v1/auth/station")
@RequiredArgsConstructor
public class StationController {
    private final StationService stationService;


    @PostMapping("/create-station")
    public ResponseEntity<BaseResponse> createStation(@RequestBody @Validated StationDto statioDto) {
            BaseResponse response = stationService.createNewStation(statioDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }





    @PutMapping("/update-station/{stationId}")
    public ResponseEntity<BaseResponse> updateStation(@PathVariable Long stationId,
                                                      @RequestBody @Validated StationDto stationDto) {
        BaseResponse response = stationService.updateStation(stationId, stationDto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete-station/{stationId}")
    public ResponseEntity<BaseResponse> deleteStation(@PathVariable Long stationId) {
        BaseResponse response = stationService.deleteStation(stationId);
        return ResponseEntity.ok(response);
    }








}
