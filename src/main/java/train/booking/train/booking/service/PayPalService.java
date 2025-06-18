package train.booking.train.booking.service;

import train.booking.train.booking.dto.PaymentRequest;

import java.util.Map;

public interface PayPalService {
    String processPaypalPayment(PaymentRequest paymentRequest);

    void processWebhook(String payload, Map<String, String> headers);
//    Payment executePaypalPayment(String paymentId, String payerId);

}
