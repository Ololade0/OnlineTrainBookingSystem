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
import train.booking.train.booking.dto.TrainDto;
import train.booking.train.booking.dto.response.BaseResponse;
import train.booking.train.booking.model.Station;
import train.booking.train.booking.model.Train;
import train.booking.train.booking.model.enums.TrainClass;
import train.booking.train.booking.service.TrainService;

import java.util.Set;

@RestController
@Slf4j
@RequestMapping("/api/v1/auth/train")
@RequiredArgsConstructor

public class TrainController {

    private final TrainService trainService;

    @PostMapping("/create-train")
    @PreAuthorize("isAuthenticated() and hasRole('ROLE_SUPERADMIN')")
    public ResponseEntity<BaseResponse> createTrain(@RequestBody @Validated TrainDto trainDto) {
        BaseResponse response = trainService.newTrain(trainDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @GetMapping("/get-all-train")
    @PreAuthorize("isAuthenticated() and hasRole('ROLE_SUPERADMIN')")
    public ResponseEntity<?> getAllTrains(@RequestParam int page, @RequestParam int size) {
        Page<Train> allTrains = trainService.getAllTrains(page, size);
        return ResponseEntity.ok(allTrains);
    }

    @GetMapping("/get-train/{trainId}")
    @PreAuthorize("isAuthenticated() and hasRole('ROLE_SUPERADMIN')")
    public ResponseEntity<?> getTrainsById(@PathVariable Long trainId) {
        Train foundTrain = trainService.getTrainById(trainId);
        return ResponseEntity.ok(foundTrain);
    }
    @GetMapping("/{trainId}/classes")
    @PreAuthorize("isAuthenticated() and hasRole('ROLE_SUPERADMIN')")
    public ResponseEntity<?> getTrainClassesInAGivenTrain(@PathVariable Long trainId){
        Set<TrainClass> getTrainClassesInAGivenTrain = trainService.getTrainClassesInAGivenTrain(trainId);
        return ResponseEntity.ok(getTrainClassesInAGivenTrain);
    }


    @PutMapping("/update-train/{trainId}")
    @PreAuthorize("isAuthenticated() and hasRole('ROLE_SUPERADMIN')")
    public ResponseEntity<BaseResponse> updateTrain(@PathVariable Long trainId,
                                                    @RequestBody @Validated TrainDto trainDto) {
        BaseResponse response = trainService.updateTrain(trainId, trainDto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete-train/{trainId}")
    @PreAuthorize("isAuthenticated() and hasRole('ROLE_SUPERADMIN')")
    public ResponseEntity<BaseResponse> deleteTrain(@PathVariable Long trainId) {
        BaseResponse response = trainService.deleteTrain(trainId);
        return ResponseEntity.ok(response);
    }



}
