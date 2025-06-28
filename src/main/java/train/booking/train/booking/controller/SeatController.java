package train.booking.train.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import train.booking.train.booking.dto.BookSeatDTO;
import train.booking.train.booking.dto.SeatDto;
import train.booking.train.booking.dto.response.BaseResponse;
import train.booking.train.booking.exceptions.SeatAlreadyReservedException;
import train.booking.train.booking.model.Booking;
import train.booking.train.booking.model.Seat;
import train.booking.train.booking.model.enums.TrainClass;
import train.booking.train.booking.service.SeatService;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/v1/auth/seat")
@RequiredArgsConstructor
public class SeatController {

    private final SeatService seatService;


    @PostMapping("/generate")
//    @PreAuthorize("hasAuthority('SUPERADMIN_ROLE')")
    public ResponseEntity<?> generateSeats(@RequestBody List<SeatDto> seatDtos,  @RequestParam long scheduleId) {
      BaseResponse generatedSeats = seatService.generateSeats(seatDtos, scheduleId);
        return new ResponseEntity<>(generatedSeats, HttpStatus.CREATED);
    }

    @PostMapping("/book-seat")
    public ResponseEntity<?> findSeatNumberByTrainClass(@RequestBody BookSeatDTO bookSeatDTO){
     Seat foundSeat = seatService.bookSeat(bookSeatDTO);
    return new ResponseEntity<>(foundSeat, HttpStatus.FOUND);
    }
    @GetMapping("/find-all-seat")
    public ResponseEntity<?> findAllSeat(@RequestParam(defaultValue = " 0" ) int page,
                                         @RequestParam(defaultValue = "10") int size){
     Page<Seat> foundSeat = seatService.findAllSeat(page, size);
     return new ResponseEntity<>(foundSeat, HttpStatus.FOUND);

    }
    @GetMapping("lock-seat/{seatNumber}")
    public ResponseEntity<?>lockSeatTemporarilyForPayment(@PathVariable  int seatNumber, @RequestParam long scheduleId,  @RequestBody TrainClass trainClass, Booking booking) throws SeatAlreadyReservedException {
        String validatedSeat = seatService.lockSeatTemporarilyForPayment(seatNumber, scheduleId, trainClass, booking);
        return ResponseEntity.ok("Seat has been temporarily reserved for 10 minutes. Please proceed to payment.");
    }
    @GetMapping("release-lock-seat")
    public ResponseEntity<?>releaseLockedSeat() {
        seatService.releaseLockedSeatAfterExpiration();
        return ResponseEntity.ok().build();
    }


}
