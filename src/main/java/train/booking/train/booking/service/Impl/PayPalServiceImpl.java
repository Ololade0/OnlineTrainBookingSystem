package train.booking.train.booking.service.Impl;

import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import train.booking.train.booking.dto.BookSeatDTO;
import train.booking.train.booking.dto.PaymentRequest;
import train.booking.train.booking.exceptions.ApprovalUrlNotFoundException;
import train.booking.train.booking.exceptions.PaymentProcessingException;
import train.booking.train.booking.model.Booking;
import train.booking.train.booking.model.BookingPayment;
import train.booking.train.booking.model.Seat;
import train.booking.train.booking.model.User;
import train.booking.train.booking.model.enums.BookingStatus;
import train.booking.train.booking.model.enums.PaymentMethod;
import train.booking.train.booking.model.enums.PaymentStatus;
import train.booking.train.booking.model.enums.SeatStatus;
import train.booking.train.booking.repository.PaymentRepository;
import train.booking.train.booking.service.BookingService;
import train.booking.train.booking.service.PayPalService;
import train.booking.train.booking.service.SeatService;
import train.booking.train.booking.service.UserService;

import java.time.LocalDateTime;
import java.util.Collections;

@Service
@Slf4j
@RequiredArgsConstructor
public class PayPalServiceImpl implements PayPalService {

    private final APIContext apiContext;
    private final PaymentRepository paymentRepository;
    private final BookingService bookingService;
    private final SeatService seatService;
    private final UserService userService;


    @Value("${paypal.cancel.url}")
    private String cancelUrl;

    @Value("${paypal.success.url}")
    private String successUrl;

    @Override
    public String processPaypalPayment(PaymentRequest request) {
        try {
            // Fetch User and Booking
            Booking booking = bookingService.findBookingById(request.getBookingId());
            User user = userService.findUserById(request.getUserId());
            // Prepare PayPal payment request
            request.setCancelUrl(cancelUrl);
            request.setSuccessUrl(successUrl);

            Payment payment = createPaypalPayment(request);
            String approvalUrl = payment.getLinks().stream()
                    .filter(link -> "approval_url".equals(link.getRel()))
                    .findFirst()
                    .map(Links::getHref)
                    .orElseThrow(() -> new ApprovalUrlNotFoundException("Approval URL not found."));

            String transactionReference = payment.getId();
            booking.setBookingStatus(BookingStatus.PENDING);

            BookingPayment bookingPayment = BookingPayment.builder()
                    .booking(booking)
                    .user(user)
                    .totalPrice(request.getTotalFare())
                    .currency(request.getCurrency().toString())
                    .transactionReference(transactionReference)
                    .paymentMethod(PaymentMethod.PAYPAL)
                    .paymentStatus(PaymentStatus.PENDING)
                    .build();

//            booking.setBookingPayment(bookingPayment);
            paymentRepository.save(bookingPayment);

            return approvalUrl + "?paymentId=" + transactionReference;

        } catch (PayPalRESTException e) {
            log.error("PayPal payment creation failed: {}", e.getMessage(), e);
            throw new PaymentProcessingException("Failed to create PayPal payment.");
        }
    }

    private Payment createPaypalPayment(PaymentRequest request) throws PayPalRESTException {
        Amount amount = new Amount();
        amount.setCurrency(request.getCurrency().toString());
        amount.setTotal(String.format("%.2f", request.getTotalFare()));

        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setDescription(request.getDescription());

        Payer payer = new Payer();
        payer.setPaymentMethod(request.getPaymentMethod().toString().toLowerCase());

        RedirectUrls redirectUrls = new RedirectUrls();
        redirectUrls.setCancelUrl(request.getCancelUrl());
        redirectUrls.setReturnUrl(request.getSuccessUrl());

        Payment payment = new Payment();
        payment.setIntent(request.getIntent());
        payment.setPayer(payer);
        payment.setTransactions(Collections.singletonList(transaction));
        payment.setRedirectUrls(redirectUrls);

        return payment.create(apiContext);
    }



    @Override
    public Payment executePaypalPayment(String paymentId, String payerId) {
        try {
            Payment payment = new Payment();
            payment.setId(paymentId);

            PaymentExecution execution = new PaymentExecution();
            execution.setPayerId(payerId);

            Payment executedPayment = payment.execute(apiContext, execution);
            log.info("PayPal payment executed. State: {}", executedPayment.getState());

            if (!"approved".equalsIgnoreCase(executedPayment.getState())) {
                throw new PaymentProcessingException("Payment not successful: " + executedPayment.getState());
            }

            BookingPayment bookingPayment = paymentRepository.findByTransactionReference(executedPayment.getId());
            if (bookingPayment == null) {
                throw new PaymentProcessingException("No payment record found for transaction: " + executedPayment.getId());
            }

            // Update payment and booking status
            bookingPayment.setPaymentStatus(PaymentStatus.COMPLETED);
            bookingPayment.setPaymentDate(LocalDateTime.now());
            bookingPayment.setSuccessUrl(successUrl);
            paymentRepository.save(bookingPayment);

            Booking booking = bookingPayment.getBooking();
            booking.setBookingStatus(BookingStatus.BOOKED);
            bookingService.updateBookingStatus(booking.getBookingId(),BookingStatus.BOOKED );

//            // Book seat
//            Seat bookedSeat = seatService.bookSeat(
//                    booking.getTrainClass(),
//                    booking.getSeatNumber()
//            );

            // Book seat
            BookSeatDTO bookSeatDTO = new BookSeatDTO();
            bookSeatDTO.setTrainClass(booking.getTrainClass());
            bookSeatDTO.setSeatNumber(booking.getSeatNumber());

            Seat bookedSeat = seatService.bookSeat(bookSeatDTO);

            bookedSeat.setSeatStatus(SeatStatus.BOOKED);
//            bookedSeat.setStatus(SeatStatus.BOOKED);
            seatService.updateSeat(bookedSeat);

            log.info("Payment completed and seat booked successfully.");
            return executedPayment;

        } catch (PayPalRESTException e) {
            log.error("Failed to execute PayPal payment: {}", e.getMessage(), e);
            throw new PaymentProcessingException("Payment execution failed.");
        }
    }
}

