package train.booking.train.booking.service;

import train.booking.train.booking.dto.PaymentRequest;

public interface StripeService {

    String processStripePayment(PaymentRequest request);
    void handleStripeWebhook(String payload, String sigHeader);

}
