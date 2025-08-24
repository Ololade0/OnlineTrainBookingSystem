package train.booking.train.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import train.booking.train.booking.dto.StationDto;
import train.booking.train.booking.dto.response.BaseResponse;
import train.booking.train.booking.model.Station;
import train.booking.train.booking.model.User;
import train.booking.train.booking.service.StationService;

import java.util.Map;

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

    @GetMapping("/get-station/{stationId}")
    @PreAuthorize("isAuthenticated() and hasRole('ROLE_SUPERADMIN')")
    public ResponseEntity<?> findStationById(@PathVariable Long stationId) {
      Station stationsPage = stationService.findStationById(stationId);
        return ResponseEntity.ok(stationsPage);
    }

    @GetMapping("/get-all-station")
    @PreAuthorize("isAuthenticated() and hasRole('ROLE_SUPERADMIN')")
    public ResponseEntity<?> getAllStations(@RequestParam int page, @RequestParam int size) {
      Page<Station> stationsPage = stationService.getAllstations(page, size);
        return ResponseEntity.ok(stationsPage);
    }


    @DeleteMapping("/delete-station/{stationId}")
    public ResponseEntity<BaseResponse> deleteStation(@PathVariable Long stationId) {
        BaseResponse response = stationService.deleteStation(stationId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/searchStations")
    @PreAuthorize("isAuthenticated() and hasRole('ROLE_SUPERADMIN')")
    public ResponseEntity<?> searchStation(@RequestParam String query,
                                         @RequestParam(defaultValue = "0")int page,
                                         @RequestParam(defaultValue = "50")int size
    ){
        Page<Station> searchStation = stationService.searchStation(query, page, size);
        if (searchStation.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "No matching station found"));
        }

        return ResponseEntity.ok(searchStation);
    }








}
