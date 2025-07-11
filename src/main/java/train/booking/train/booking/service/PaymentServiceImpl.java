package train.booking.train.booking.service;

import com.stripe.exception.StripeException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import train.booking.train.booking.dto.PaymentRequest;
import train.booking.train.booking.exceptions.PaymentProcessingException;
import train.booking.train.booking.model.BookingPayment;
import train.booking.train.booking.model.enums.PaymentStatus;
import train.booking.train.booking.repository.PaymentRepository;

import java.io.IOException;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PayPalService payPalService;
    private final PayStackService payStackService;
    private final StripeService stripeService;
    private final PaymentRepository paymentRepository;


    @Override
    public String paymentProcessing(PaymentRequest paymentRequest) throws IOException, InterruptedException, StripeException {
        return switch (paymentRequest.getPaymentMethod()) {
            case PAYSTACK -> payStackService.processPayStackPayment(paymentRequest);
            case PAYPAL -> payPalService.processPaypalPayment(paymentRequest);
            case STRIPE -> stripeService.processStripePayment(paymentRequest);
            default -> throw new PaymentProcessingException("Unsupported Payment Method" + paymentRequest.getPaymentMethod());
        };
    }

    @Override
    public BookingPayment findByTransactionReference(String paymentId) {
        BookingPayment bookingPayment = paymentRepository.findByTransactionReference(paymentId);
        if (bookingPayment == null) {
            throw new PaymentProcessingException("Payment with Transaction refernec " + paymentId + " cannot be found");
        }
        return bookingPayment;
    }

    @Override
    public BookingPayment save(BookingPayment bookingPayment) {
        return paymentRepository.save(bookingPayment);

    }
    @Override

    public BookingPayment updateBookingPayment(String transactionReference) {
        BookingPayment bookingPayment = paymentRepository.findByTransactionReference(transactionReference);
        if (bookingPayment == null) {
            throw new PaymentProcessingException("Booking payment cannot be found");
        }
            bookingPayment.setPaymentStatus(PaymentStatus.COMPLETED);
            bookingPayment.setPaymentDate(LocalDateTime.now());
            return paymentRepository.save(bookingPayment);
        }



}





