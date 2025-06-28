package train.booking.train.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import train.booking.train.booking.dto.PaymentRequest;
import train.booking.train.booking.exceptions.PaymentProcessingException;
import train.booking.train.booking.repository.PaymentRepository;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;

    private final PayPalService payPalService;
    private final PayStackService payStackService;



    @Override
    public String paymentProcessing(PaymentRequest paymentRequest) throws IOException, InterruptedException {
       return switch (paymentRequest.getPaymentMethod()){
           case PAYSTACK -> payStackService.processPayStackPayment(paymentRequest);
            case PAYPAL -> payPalService.processPaypalPayment(paymentRequest);
            default -> throw new PaymentProcessingException("Unsupported Payment Method" + paymentRequest.getPaymentMethod());
        };
    }



}
