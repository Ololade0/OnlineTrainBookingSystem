package train.booking.train.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import train.booking.train.booking.dto.BookingRequestDTO;
import train.booking.train.booking.service.BookingService;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/auth/booking")
public class BookingController {

    private final BookingService bookingService;


    @PostMapping("/book")

    public ResponseEntity<String> createBooking(@RequestBody BookingRequestDTO bookingRequestDTO) {
        bookingService.createBooking(bookingRequestDTO);
        log.info("PRICE LISR. {}", bookingRequestDTO.getScheduleId());
        return ResponseEntity.ok("Booking request received and is being processed.");
    }

}



