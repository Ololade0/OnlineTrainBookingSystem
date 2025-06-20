package train.booking.train.booking.service.Impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import train.booking.train.booking.dto.PaymentRequest;
import train.booking.train.booking.dto.PaymentSuccessDTO;
import train.booking.train.booking.exceptions.ApprovalUrlNotFoundException;
import train.booking.train.booking.exceptions.PaymentProcessingException;
import train.booking.train.booking.model.Booking;
import train.booking.train.booking.model.BookingPayment;
import train.booking.train.booking.model.User;
import train.booking.train.booking.model.enums.BookingStatus;
import train.booking.train.booking.model.enums.Currency;
import train.booking.train.booking.model.enums.PaymentMethod;
import train.booking.train.booking.model.enums.PaymentStatus;
import train.booking.train.booking.repository.PaymentRepository;
import train.booking.train.booking.service.BookingService;
import train.booking.train.booking.service.PayPalService;
import train.booking.train.booking.service.SeatService;
import train.booking.train.booking.service.UserService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class PayPalServiceImpl implements PayPalService {

    private final APIContext apiContext;
    private final PaymentRepository paymentRepository;
    private final BookingService bookingService;
    private final SeatService seatService;
    private final UserService userService;
    private final JmsTemplate jmsTemplate;

    @Value("${paypal.cancel.url}")
    private String cancelUrl;

    @Value("${paypal.success.url}")
    private String successUrl;
@Transactional
    @Override
    public String processPaypalPayment(PaymentRequest request) {
        try {
            User user = userService.findUserById(request.getUserId());
            Optional<Booking> bookingOpt = bookingService.findBookingByBookingNumber(request.getPnrCode());
            Booking booking = bookingOpt.get();
            if(booking.getBookingStatus() != BookingStatus.RESERVED){
                throw new PaymentProcessingException("You cannot make payment for already booked seat");

            }

            BigDecimal totalFare = booking.getTotalFareAmount();

            Payment payment = createPaypalPayment(request.getPaymentMethod(), totalFare);

            String approvalUrl = payment.getLinks().stream()
                    .filter(link -> "approval_url".equals(link.getRel()))
                    .findFirst()
                    .map(Links::getHref)
                    .orElseThrow(() -> new ApprovalUrlNotFoundException("Approval URL not found."));
            String transactionReference = payment.getId();
            savePaymentDetails(booking, user, totalFare, transactionReference);

                return approvalUrl;



        } catch (PayPalRESTException e) {
            log.error("PayPal payment creation failed: {}", e.getMessage(), e);
            throw new PaymentProcessingException("Failed to create PayPal payment.");
        }
    }

    private void savePaymentDetails(Booking booking, User user, BigDecimal totalFare, String transactionReference) {
        BookingPayment bookingPayment = BookingPayment.builder()
                .booking(booking)
                .user(user)
                .totalPrice(totalFare)
                .currency(Currency.USD.name())
                .transactionReference(transactionReference)
                .paymentMethod(PaymentMethod.PAYPAL)
                .paymentStatus(PaymentStatus.PENDING)
                .build();
        booking.setBookingPayment(bookingPayment);
        paymentRepository.save(bookingPayment);
    }

    private Payment createPaypalPayment(PaymentMethod paymentMethod, BigDecimal totalFare) throws PayPalRESTException {
        Amount amount = new Amount();
        amount.setCurrency(Currency.USD.name());
        amount.setTotal(String.format("%.2f", totalFare));

        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setDescription("Train Booking System");

        Payer payer = new Payer();
        payer.setPaymentMethod(paymentMethod.toString().toLowerCase());

        Payment payment = new Payment();
        payment.setIntent("sale");
        payment.setPayer(payer);
        payment.setTransactions(Collections.singletonList(transaction));

        RedirectUrls redirectUrls = new RedirectUrls();
        redirectUrls.setCancelUrl(cancelUrl);
        redirectUrls.setReturnUrl(successUrl);
        payment.setRedirectUrls(redirectUrls);
        return payment.create(apiContext);
    }
@Transactional
    @Override
    public void verifyPayment(String payload) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> webhookEvent = objectMapper.readValue(payload, Map.class);

            String eventType = (String) webhookEvent.get("event_type");
            if (!"PAYMENT.SALE.COMPLETED".equals(eventType)) {
                log.info("Ignoring webhook event: {}", eventType);
                return;
            }

            Map<String, Object> resource = (Map<String, Object>) webhookEvent.get("resource");
            log.info("Received resource payload: {}", resource);

            String transactionId = (String) resource.get("parent_payment");
            if (transactionId == null) {
             transactionId = (String) resource.get("id");
            }

            BookingPayment bookingPayment = paymentRepository.findByTransactionReference(transactionId);
            if (bookingPayment == null) {
                log.warn("Webhook received for unknown transaction ID: {}", transactionId);
                throw new PaymentProcessingException("Payment record not found.");
            }

            if (bookingPayment.getPaymentStatus() == PaymentStatus.COMPLETED) {
                log.info("Payment already completed for transaction ID: {}", transactionId);
                return;
            }

            bookingPayment.setPaymentStatus(PaymentStatus.COMPLETED);
            bookingPayment.setSuccessUrl(successUrl);
            bookingPayment.setCancelUrl(cancelUrl);
            bookingPayment.setPaymentDate(LocalDateTime.now());
            paymentRepository.save(bookingPayment);
            PaymentSuccessDTO dto = PaymentSuccessDTO.builder()
                    .bookingId(bookingPayment.getBooking().getBookingId())
                    .pnrCode(bookingPayment.getBooking().getBookingNumber())
                    .paymentId(transactionId)
                    .build();

            String jsonPayload = objectMapper.writeValueAsString(dto);
            jmsTemplate.convertAndSend("payment-queue", jsonPayload);

            log.info("Processed payment and pushed to queue for booking ID: {}", bookingPayment.getBooking().getBookingId());

        } catch (Exception e) {
            log.error("Failed to process PayPal webhook: {}", e.getMessage(), e);
            jmsTemplate.convertAndSend("payment-failure-queue", payload);
            throw new PaymentProcessingException("Webhook processing failed.");
        }
    }
}
