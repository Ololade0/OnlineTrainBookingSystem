package train.booking.train.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import train.booking.train.booking.dto.TrainDto;
import train.booking.train.booking.dto.response.BaseResponse;
import train.booking.train.booking.service.ScheduleService;
import train.booking.train.booking.service.TrainService;

@RestController
@Slf4j
@RequestMapping("/api/v1/auth/train")
@RequiredArgsConstructor

public class TrainController {

    private final TrainService trainService;

    @PostMapping("/create-train")
    public ResponseEntity<BaseResponse> createTrain(@RequestBody @Validated TrainDto trainDto) {
        BaseResponse response = trainService.newTrain(trainDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
