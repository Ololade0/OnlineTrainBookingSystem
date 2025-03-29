package train.booking.train.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import train.booking.train.booking.dto.SeatDto;
import train.booking.train.booking.dto.response.BaseResponse;
import train.booking.train.booking.model.Seat;
import train.booking.train.booking.service.SeatService;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class SeatController {

    private final SeatService seatService;


    @PostMapping("/generate")
//    @PreAuthorize("hasAuthority('SUPERADMIN_ROLE')")
    public ResponseEntity<?> generateSeats(@RequestBody List<SeatDto> seatDtos) {
      BaseResponse generatedSeats = seatService.generateSeats(seatDtos);
        return new ResponseEntity<>(generatedSeats, HttpStatus.CREATED);
    }


}
