package train.booking.train.booking.listener;

import lombok.RequiredArgsConstructor;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import train.booking.train.booking.dto.PaymentRequest;
import train.booking.train.booking.service.PaymentService;

@Component
@RequiredArgsConstructor
public class PaymentListener {
    private final PaymentService paymentService;
//    private final BookingService bookingService;

    @JmsListener(destination = "payment-Queue")
    public void processPayPalPaymentQueue(PaymentRequest paymentRequest) {
//      Booking foundBooking =  bookingService.findBookingById(paymentRequest.getBookingId());
        paymentService.paymentProcessing(paymentRequest);


    }
}
