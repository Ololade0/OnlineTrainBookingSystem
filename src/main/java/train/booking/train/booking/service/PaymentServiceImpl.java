package train.booking.train.booking.service;

import com.stripe.exception.StripeException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import train.booking.train.booking.dto.PaymentRequest;
import train.booking.train.booking.exceptions.PaymentProcessingException;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PayPalService payPalService;
    private final PayStackService payStackService;
    private final StripeService stripeService;


    @Override
    public String paymentProcessing(PaymentRequest paymentRequest) throws IOException, InterruptedException, StripeException {
        return switch (paymentRequest.getPaymentMethod()) {
            case PAYSTACK -> payStackService.processPayStackPayment(paymentRequest);
            case PAYPAL -> payPalService.processPaypalPayment(paymentRequest);
            case STRIPE -> stripeService.processStripePayment(paymentRequest);
            default -> throw new PaymentProcessingException("Unsupported Payment Method" + paymentRequest.getPaymentMethod());
        };
    }



}



