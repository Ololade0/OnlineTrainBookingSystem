package train.booking.train.booking.service;

import train.booking.train.booking.dto.PaymentRequest;

public interface PayPalService {
    String processPaypalPayment(PaymentRequest paymentRequest);

    void verifyPayment(String payload);

}
