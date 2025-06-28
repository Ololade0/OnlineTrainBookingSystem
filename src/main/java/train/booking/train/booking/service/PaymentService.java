package train.booking.train.booking.service;

import train.booking.train.booking.dto.PaymentRequest;

import java.io.IOException;

public interface PaymentService {

        String paymentProcessing(PaymentRequest paymentRequest) throws IOException, InterruptedException;
//}
}
