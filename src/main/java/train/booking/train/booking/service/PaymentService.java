package train.booking.train.booking.service;

import com.stripe.exception.StripeException;
import train.booking.train.booking.dto.PaymentRequest;

import java.io.IOException;

public interface PaymentService {

        String paymentProcessing(PaymentRequest paymentRequest) throws IOException, InterruptedException, StripeException;



//}
}
