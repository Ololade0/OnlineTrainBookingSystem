package train.booking.train.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import train.booking.train.booking.model.Seat;
import train.booking.train.booking.model.TrainClass;
import train.booking.train.booking.service.SeatService;

import java.util.List;

@RestController
@RequestMapping("api/seat")
@RequiredArgsConstructor
public class SeatController {

    private final SeatService seatService;
@PostMapping("/create-seat")
    public ResponseEntity<?> generateSeat(
                                          @RequestParam int startSeat,
                                          @RequestParam int endSeat,
                                          @RequestParam TrainClass trainClass){

        List<Seat> generatedSeat =seatService.generateSeats(startSeat,endSeat, trainClass);
        return new ResponseEntity<>(generatedSeat, HttpStatus.CREATED);

    }

}
