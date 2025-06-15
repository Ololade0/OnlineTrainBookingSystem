//package train.booking.train.booking.controller;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.jms.core.JmsTemplate;
//import org.springframework.web.bind.annotation.*;
//import train.booking.train.booking.dto.PaymentRequest;
//import train.booking.train.booking.dto.response.PaymentVerificationResponse;
//import train.booking.train.booking.model.Booking;
//import train.booking.train.booking.model.enums.BookingStatus;
//import train.booking.train.booking.model.enums.PaymentMethod;
//import train.booking.train.booking.service.BookingService;
//import train.booking.train.booking.service.PaymentService;
//import train.booking.train.booking.service.PaymentServiceImpl;
//
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/api/payments")
//public class PaymentController {
//
//    private final PaymentServiceImpl paymentServiceFactory;
//    private final BookingService bookingService;
//    private final JmsTemplate jmsTemplate;
//
//    @PostMapping("/initiate")
//    public ResponseEntity<String> initiatePayment(@RequestBody PaymentRequest request) {
//        Booking booking = bookingService.findBookingById(request.getBookingId());
//        if (booking == null || booking.getBookingStatus() != BookingStatus.PENDING) {
//            return ResponseEntity.badRequest().body("Invalid booking")
//
//        }
//
////        PaymentService service = paymentServiceFactory.getService(request.getPaymentMethod());
////        String redirectUrl = service.initiatePayment(request);
////
////        return ResponseEntity.ok(redirectUrl);
//    }
////
////    @GetMapping("/verify")
////    public ResponseEntity<String> verifyPayment(@RequestParam String transactionId,
////                                                @RequestParam PaymentMethod channel) {
////        PaymentService service = paymentServiceFactory.getService(channel);
////        PaymentVerificationResponse response = service.verifyPayment(transactionId);
////
////        if (!response.isSuccess()) return ResponseEntity.status(400).body("Payment failed");
////
////        jmsTemplate.convertAndSend("paymentQueue", response);
////        return ResponseEntity.ok("Payment verified, booking will be confirmed");
////    }
//    }
//
//}
