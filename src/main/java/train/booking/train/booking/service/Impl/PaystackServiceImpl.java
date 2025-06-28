package train.booking.train.booking.service.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import train.booking.train.booking.dto.PaymentRequest;
import train.booking.train.booking.service.PayStackService;

@Slf4j
@RequiredArgsConstructor
@Service
public class PaystackServiceImpl implements PayStackService {


    @Override
    public String processPayStackPayment(PaymentRequest paymentRequest) {
        return null;
    }
}