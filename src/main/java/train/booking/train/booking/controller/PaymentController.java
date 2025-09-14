package train.booking.train.booking.controller;

import com.stripe.exception.StripeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import train.booking.train.booking.dto.PaymentRequest;
import train.booking.train.booking.model.enums.PaymentMethod;
import train.booking.train.booking.service.PayPalService;
import train.booking.train.booking.service.PayStackService;
import train.booking.train.booking.service.PaymentService;
import train.booking.train.booking.service.StripeService;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth/payments")
@Slf4j
public class PaymentController {


    private final StripeService stripeService;

    private final PayPalService payPalService;

    private final PaymentService paymentService;
    private final PayStackService payStackService;

@GetMapping("/get-all-paymentMethods")
    public ResponseEntity<?> getAllPaymentMethod(){
        List<PaymentMethod> paymentMethodList = paymentService.getAllPaymentMethod();
        return ResponseEntity.ok(paymentMethodList);
    }


    @PostMapping("/payment")
    public ResponseEntity<String> processPayment(@RequestBody PaymentRequest paymentRequest) throws IOException, InterruptedException, StripeException {

        String paymentRedirectUrl = paymentService.paymentProcessing(paymentRequest);
        return ResponseEntity.ok(paymentRedirectUrl);
    }

    @PostMapping("/webhook/paypal")
    public ResponseEntity<String> handleWebhook(@RequestBody String payload) {

        payPalService.verifyPayment(payload);
        log.info("Webhook payload: {}", payload);
        return ResponseEntity.ok("Webhook processed successfully");

    }

    @PostMapping("/webhook/paystack")
    public ResponseEntity<Void> handlePaystackWebhook(
            @RequestBody String payload,
            @RequestHeader(value = "X-Paystack-Signature", required = false) String signature) {
         try {
            payStackService.verifyPaystackWebhook(payload, signature);
        } catch (Exception e) {
            log.error("💥 Webhook handling failed", e);
            return ResponseEntity.internalServerError().build();
        }

        return ResponseEntity.ok().build();
    }

    @PostMapping("webhook/stripe")
    public ResponseEntity<String> handleStripeWebhook(@RequestBody String payload,
                                                      @RequestHeader("Stripe-Signature") String sigHeader) {
        log.info("Received Stripe webhook: {}", payload);
        stripeService.handleStripeWebhook(payload, sigHeader);
        return ResponseEntity.ok("Webhook received and processed");
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


