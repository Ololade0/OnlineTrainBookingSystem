package train.booking.train.booking.service;

import train.booking.train.booking.dto.PaymentRequest;

public interface PayStackService {
    String processPayStackPayment(PaymentRequest paymentRequest);
}
