package train.booking.train.booking.service;

import com.paypal.api.payments.Payment;
import train.booking.train.booking.dto.PaymentRequest;

public interface PayPalService {
    String processPaypalPayment(PaymentRequest paymentRequest);
    Payment executePaypalPayment(String paymentId, String payerId);
}
