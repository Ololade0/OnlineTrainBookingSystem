package train.booking.train.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import train.booking.train.booking.dto.BookingRequestDTO;
import train.booking.train.booking.dto.BookingResponse;
import train.booking.train.booking.model.Booking;
import train.booking.train.booking.repository.BookingRepository;
import train.booking.train.booking.service.BookingService;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/auth/booking")
public class BookingController {

    private final BookingService bookingService;
    private final BookingRepository bookingRepository;


    @PostMapping("/book")

    public ResponseEntity<?> createBooking(@RequestBody BookingRequestDTO bookingRequestDTO) {
     BookingResponse bookingResponse = bookingService.createBooking(bookingRequestDTO);
        return ResponseEntity.ok(bookingResponse);


//        return ResponseEntity.ok(bookingResponse);
    }

    @GetMapping("/pnr/{pnrCode}")
    public ResponseEntity<?> getBookingIdByPnr(@PathVariable String pnrCode) {
        Booking booking = bookingRepository.findByBookingNumber(pnrCode);
        return ResponseEntity.ok(booking);
    }


}



