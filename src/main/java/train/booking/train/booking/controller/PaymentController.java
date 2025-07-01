package train.booking.train.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import train.booking.train.booking.dto.PaymentRequest;
import train.booking.train.booking.service.PayPalService;
import train.booking.train.booking.service.PaymentService;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth/payments")
@Slf4j
public class PaymentController {

    private final PayPalService payPalService;

    private final PaymentService paymentService;


    @PostMapping("/payment")
    public ResponseEntity<String> processPayment(@RequestBody PaymentRequest paymentRequest) throws IOException, InterruptedException {
        String paymentRedirectUrl = paymentService.paymentProcessing(paymentRequest);
        return ResponseEntity.ok(paymentRedirectUrl);
    }

     @PostMapping("/webhook/paypal")
        public ResponseEntity<String> handleWebhook(@RequestBody String payload) {
         payPalService.verifyPayment(payload);
         log.info("Webhook payload: {}", payload);
         return ResponseEntity.ok("Webhook processed successfully");

}




    @GetMapping("/cancel")
    public ResponseEntity<String> cancel() {
        return ResponseEntity.ok("Payment was cancelled by the user.");
    }
@GetMapping("/success")
public ResponseEntity<String> successNotice() {
    return ResponseEntity.ok("Thanks! We're verifying your payment.");
}


}


