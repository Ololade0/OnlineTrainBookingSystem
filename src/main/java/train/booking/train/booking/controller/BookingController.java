package train.booking.train.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import train.booking.train.booking.dto.BookingRequestDTO;
import train.booking.train.booking.service.BookingService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth/booking")
public class BookingController {

    private final BookingService bookingService;


    @PostMapping("/book")

    public ResponseEntity<String> createBooking(@RequestBody BookingRequestDTO bookingRequestDTO) {
        bookingService.createBooking(bookingRequestDTO);
        return ResponseEntity.ok("Booking request received and is being processed.");
    }

}




//    @PostMapping("/initiate")
//    public ResponseEntity<String> initiatePayment(@RequestBody PaymentRequest paymentRequestDTO) {
//        Booking booking = bookingService.findBookingById(paymentRequestDTO.getBookingId());
//
//        if (booking == null || booking.getBookingStatus() != BookingStatus.RESERVED) {
//            return ResponseEntity.badRequest().body("Invalid booking or already paid.");
//        }
//
//        // Send to ActiveMQ
//        bookingService.sendPaymentRequest(paymentRequestDTO);
//
//        return ResponseEntity.ok("Payment initiated. Processing...");
//    }
//}


//    @PostMapping("/send")
//    public String sendPayment() {
//        PaymentRequest message = new PaymentRequest(amount, userId, bookingId);
//        paymentService.sendPaymentMessage(message);
//        return "Payment request sent for booking ID: " + bookingId;
//    }
//
//}
