package train.booking.train.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import train.booking.train.booking.dto.BookSeatDTO;
import train.booking.train.booking.dto.GenerateSeatDto;
import train.booking.train.booking.dto.response.BaseResponse;
import train.booking.train.booking.exceptions.SeatAlreadyReservedException;
import train.booking.train.booking.model.Booking;
import train.booking.train.booking.model.Seat;
import train.booking.train.booking.model.enums.TrainClass;
import train.booking.train.booking.service.SeatService;

import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/api/v1/auth/seat")
@RequiredArgsConstructor
public class SeatController {

    private final SeatService seatService;


    @PostMapping("/generate")
    @PreAuthorize("isAuthenticated() and hasRole('ROLE_SUPERADMIN')")
    public ResponseEntity<?> generateSeats(@RequestBody List<GenerateSeatDto> seatDtos, @RequestParam long trainId) {
      BaseResponse generatedSeats = seatService.generateSeats(seatDtos, trainId);
        return new ResponseEntity<>(generatedSeats, HttpStatus.CREATED);
    }

    @PostMapping("/book-seat")
    public ResponseEntity<?> findSeatNumberByTrainClass(@RequestBody BookSeatDTO bookSeatDTO){
     Seat foundSeat = seatService.bookSeat(bookSeatDTO);
    return new ResponseEntity<>(foundSeat, HttpStatus.FOUND);
    }
    @GetMapping("/find-all-seat")
    public ResponseEntity<?> findAllSeat(@RequestParam(defaultValue = " 0" ) int page,
                                         @RequestParam(defaultValue = "50") int size){
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

        @GetMapping("/{trainId}/seats/summary")
        public ResponseEntity<?> getSeatSummary(@PathVariable Long trainId) {
            List<Map<String, Object>>  list = seatService.getSeatSummary(trainId);
           return ResponseEntity.ok(list);
    }



}
