//package train.booking.train.booking.service.Impl;
//
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.stripe.Stripe;
//import com.stripe.model.PaymentIntent;
//import com.stripe.param.PaymentIntentCreateParams;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.jms.core.JmsTemplate;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import train.booking.train.booking.dto.PaymentRequest;
//import train.booking.train.booking.dto.PaymentSuccessDTO;
//import train.booking.train.booking.exceptions.PaymentProcessingException;
//import train.booking.train.booking.model.Booking;
//import train.booking.train.booking.model.BookingPayment;
//import train.booking.train.booking.model.User;
//import train.booking.train.booking.model.enums.BookingStatus;
//import train.booking.train.booking.model.enums.PaymentMethod;
//import train.booking.train.booking.model.enums.PaymentStatus;
//import train.booking.train.booking.repository.PaymentRepository;
//import train.booking.train.booking.service.BookingService;
//import train.booking.train.booking.service.NotificationService;
//import train.booking.train.booking.service.StripeService;
//import train.booking.train.booking.service.UserService;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//
//@Service
//@RequiredArgsConstructor
//@Slf4j
//public class StripeServiceImpl implements StripeService {
//
//    @Value("${stripe.secret.key}")
//    private String secretKey;
//
//    private final BookingService bookingService;
//    private final UserService userService;
//    private final PaymentRepository paymentRepository;
//    private final JmsTemplate jmsTemplate;
//    private final NotificationService notificationService;
//    private final ObjectMapper objectMapper;
//
//    @Override
//    public String processStripePayment(PaymentRequest request) {
//        try {
//            Booking booking = bookingService.findBookingByBookingNumber(request.getPnrCode())
//                    .orElseThrow(() -> new RuntimeException("Booking not found"));
//
//            if (booking.getBookingStatus() != BookingStatus.RESERVED) {
//                throw new PaymentProcessingException("Cannot pay for already booked ticket.");
//            }
//
//            BigDecimal totalFare = booking.getTotalFareAmount();
//            User user = userService.findUserById(request.getUserId());
//
//
//            // Set Stripe API key
//            Stripe.apiKey = secretKey;
//
//            // Create payment intent
//            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
//                    .setAmount(totalFare.multiply(BigDecimal.valueOf(100)).longValue()) // Convert to smallest currency unit
//                    .setCurrency("usd")
//                    .setReceiptEmail(user.getEmail())
//                    .build();
//
//            PaymentIntent paymentIntent = PaymentIntent.create(params);
//            String transactionRef = paymentIntent.getId();
//
//            savePaymentDetails(booking, user, totalFare, transactionRef);
//
//            // Return client secret for confirmation on frontend
//            return paymentIntent.getClientSecret();
//
//        } catch (Exception e) {
//            log.error("Error initializing Stripe payment", e);
//            throw new PaymentProcessingException("Failed to initialize Stripe payment");
//        }
//    }
//    private void savePaymentDetails(Booking booking, User user, BigDecimal totalFare, String reference) {
//        BookingPayment bookingPayment = BookingPayment.builder()
//                .booking(booking)
//                .user(user)
//                .totalPrice(totalFare)
//                .transactionReference(reference)
//                .paymentMethod(PaymentMethod.STRIPE)
//                .paymentStatus(PaymentStatus.PENDING)
//                .build();
//        booking.setBookingPayment(bookingPayment);
//        paymentRepository.save(bookingPayment);
//    }
//
//    @Transactional
//    @Override
//    public void verifyStripeWebhook(String payload) {
//        try {
//            JsonNode json = objectMapper.readTree(payload);
//            String eventType = json.get("type").asText();
//            if (!"payment_intent.succeeded".equals(eventType)) {
//                log.info("Unhandled event: {}", eventType);
//                return;
//            }
//
//            String reference = json.get("data").get("object").get("id").asText();
//            BookingPayment payment = paymentRepository.findByTransactionReference(reference);
//            if (payment == null) {
//                log.warn("No payment found for reference: {}", reference);
//                return;
//            }
//
//            if (payment.getPaymentStatus() == PaymentStatus.COMPLETED) {
//                log.info("Payment already processed for reference: {}", reference);
//                return;
//            }
//
//            payment.setPaymentStatus(PaymentStatus.COMPLETED);
//            payment.setPaymentDate(LocalDateTime.now());
//            paymentRepository.save(payment);
//
//            Booking booking = payment.getBooking();
//            PaymentSuccessDTO dto = PaymentSuccessDTO.builder()
//                    .bookingId(booking.getBookingId())
//                    .pnrCode(booking.getBookingNumber())
//                    .paymentId(reference)
//                    .build();
//
//            notificationService.sendBookingReceipts(payment.getUser().getEmail(),
//                    bookingService.generateBookingReceipt(booking.getBookingId()));
//
//            jmsTemplate.convertAndSend("payment-queue", objectMapper.writeValueAsString(dto));
//            log.info("Stripe payment verified and pushed to queue");
//
//        } catch (Exception e) {
//            log.error("Failed to process Stripe webhook", e);
//            jmsTemplate.convertAndSend("payment-failure-queue", payload);
//        }
//    }
//}


package train.booking.train.booking.service.Impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import train.booking.train.booking.dto.PaymentRequest;
import train.booking.train.booking.dto.PaymentSuccessDTO;
import train.booking.train.booking.exceptions.PaymentProcessingException;
import train.booking.train.booking.model.Booking;
import train.booking.train.booking.model.BookingPayment;
import train.booking.train.booking.model.User;
import train.booking.train.booking.model.enums.BookingStatus;
import train.booking.train.booking.model.enums.PaymentMethod;
import train.booking.train.booking.model.enums.PaymentStatus;
import train.booking.train.booking.repository.PaymentRepository;
import train.booking.train.booking.service.BookingService;
import train.booking.train.booking.service.StripeService;
import train.booking.train.booking.service.UserService;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class StripeServiceImpl implements StripeService {

    @Value("${stripe.secret.key}")
    private String secretKey;

    @Value("${stripe.webhook.secret}")
    private String stripeWebhookSecret;


    @Value("${paypal.cancel.url}")
    private String cancelUrl;

    @Value("${paypal.success.url}")
    private String successUrl;
    private final BookingService bookingService;
    private final UserService userService;
    private final PaymentRepository paymentRepository;
    private final JmsTemplate jmsTemplate;
    private final ObjectMapper objectMapper;

    @PostConstruct
    public void init() {
        Stripe.apiKey = secretKey;
    }

    @Override
    public String processStripePayment(PaymentRequest request) {
        try {
            Booking booking = bookingService.findBookingByBookingNumber(request.getPnrCode())
                    .orElseThrow(() -> new RuntimeException("Booking not found"));

            if (booking.getBookingStatus() != BookingStatus.RESERVED) {
                throw new PaymentProcessingException("Cannot pay for already booked ticket.");
            }

            User user = userService.findUserById(request.getUserId());
            BigDecimal amount = booking.getTotalFareAmount();

            SessionCreateParams params = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl(successUrl + "?session_id={CHECKOUT_SESSION_ID}")
                    .setCancelUrl(cancelUrl)
                    .setCustomerEmail(user.getEmail())
                    .addLineItem(SessionCreateParams.LineItem.builder()
                            .setQuantity(1L)
                            .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                                    .setCurrency("NGN")
                                    .setUnitAmount(amount.multiply(BigDecimal.valueOf(100)).longValue())
                                    .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                            .setName("Train Ticket - " + booking.getBookingNumber())
                                            .build())
                                    .build())
                            .build())
                    .putMetadata("bookingId", booking.getBookingId().toString())
                    .build();

            Session session = Session.create(params);
            savePaymentDetails(booking, user, amount, session.getId());
            return session.getUrl();

        } catch (Exception e) {
            log.error("Stripe session creation failed", e);
            throw new PaymentProcessingException("Unable to initialize Stripe payment.");
        }
    }

    private void savePaymentDetails(Booking booking, User user, BigDecimal amount, String reference) {
        BookingPayment payment = BookingPayment.builder()
                .booking(booking)
                .user(user)
                .totalPrice(amount)
                .transactionReference(reference)
                .paymentStatus(PaymentStatus.PENDING)
                .paymentMethod(PaymentMethod.STRIPE)
                .build();
        booking.setBookingPayment(payment);
        paymentRepository.save(payment);
    }

    @Transactional
    @Override
    public void handleStripeWebhook(String payload, String sigHeader) {
        try {

            com.stripe.model.Event event = Webhook.constructEvent(
                    payload,
                    sigHeader, stripeWebhookSecret);


            if ("checkout.session.completed".equals(event.getType())) {
                Session session = (Session) event.getDataObjectDeserializer().getObject().orElse(null);
                if (session == null) return;

                String bookingIdStr = session.getMetadata().get("bookingId");

                if (bookingIdStr == null || bookingIdStr.isBlank()) {
                    log.warn("Stripe session metadata missing bookingId: sessionId={}", session.getId());
                    return;
                }

                Long bookingId = Long.valueOf(bookingIdStr);
                Booking booking = bookingService.findBookingById(bookingId);

                if (booking == null) {
                    log.warn("Booking not found for ID: {}", bookingId);
                    return;
                }


                BookingPayment payment = booking.getBookingPayment();
                if (payment == null) {
                    log.warn("No payment found for booking {}", bookingId);
                    return;
                }

                if (payment.getPaymentStatus() == PaymentStatus.COMPLETED) {
                    log.info("Payment already processed for: {}", payment.getTransactionReference());
                    return;
                }
                PaymentSuccessDTO dto = PaymentSuccessDTO.builder()
                        .bookingId(bookingId)
                        .pnrCode(booking.getBookingNumber())
                        .paymentId(payment.getTransactionReference())
                        .build();
                jmsTemplate.convertAndSend("payment-queue", objectMapper.writeValueAsString(dto));

                log.info("Parsed bookingId from metadata: {}", session.getMetadata().get("bookingId"));


            }

        }

        catch (Exception e) {
            log.error("Error processing Stripe webhook", e);
            jmsTemplate.convertAndSend("payment-failure-queue", payload);
        }


    }

}


