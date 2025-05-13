package train.booking.train.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import train.booking.train.booking.dto.BookingDTO;
import train.booking.train.booking.model.Booking;
import train.booking.train.booking.service.BookingService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth/booking")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping("/book")

    public ResponseEntity<?> createBooking(@RequestBody BookingDTO bookingDTO){
//                                           @RequestParam Long departureStationId,
//                                           @RequestParam Long arrivalStationId,
//                                           @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE )LocalDate  departureDate){

//        CreateScheduleBookingDTO createScheduleBookingDTO = new CreateScheduleBookingDTO(
//                departureStationId,
//               arrivalStationId,
//                departureDate);
     Booking createBooking  =  bookingService.createBooking(bookingDTO);
     return ResponseEntity.ok(createBooking);


    }

}
