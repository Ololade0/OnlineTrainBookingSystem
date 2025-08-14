package train.booking.train.booking.service.Impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import train.booking.train.booking.service.PayStackService;
import train.booking.train.booking.service.UserService;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaystackServiceImpl implements PayStackService {

    @Value("${paystack.secret.key}")
    private String secretKey;

    @Value("${paystack.init.url}")
    private String paystackInitUrl;

    private final BookingService bookingService;
    private final UserService userService;
    private final PaymentRepository paymentRepository;
    private final JmsTemplate jmsTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public String processPayStackPayment(PaymentRequest request) {
        try {
            Booking booking = bookingService.findBookingByBookingNumber(request.getPnrCode())
                    .orElseThrow(() -> new RuntimeException("Booking not found"));

            if (booking.getBookingStatus() != BookingStatus.RESERVED) {
                throw new PaymentProcessingException("Cannot pay for already booked ticket.");
            }

            BigDecimal totalFare = booking.getTotalFareAmount();
            User user = userService.findUserById(request.getUserId());

            // Create initialization payload
            Map<String, Object> payload = Map.of(
                    "email", user.getEmail(),
                    "amount", totalFare.multiply(BigDecimal.valueOf(100)).intValue(), // Convert to kobo
                    "currency", "NGN",
                    "reference", generateTransactionRef()
            );

            // Call Paystack API
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(paystackInitUrl))
                    .header("Authorization", "Bearer " + secretKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(payload)))
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            JsonNode jsonResponse = objectMapper.readTree(response.body());
            if (!jsonResponse.get("status").asBoolean()) {
                throw new PaymentProcessingException("Failed to initialize Paystack payment");
            }

            String authorizationUrl = jsonResponse.get("data").get("authorization_url").asText();
            String transactionRef = jsonResponse.get("data").get("reference").asText();

            savePaymentDetails(booking, user, totalFare, transactionRef);

            return authorizationUrl;

        } catch (Exception e) {
            log.error("Error initializing Paystack payment", e);
            throw new PaymentProcessingException("Failed to initialize Paystack payment");
        }
    }

    private void savePaymentDetails(Booking booking, User user, BigDecimal totalFare, String reference) {
        BookingPayment bookingPayment = BookingPayment.builder()
                .booking(booking)
                .user(user)
                .totalPrice(totalFare)
                .transactionReference(reference)
                .paymentMethod(PaymentMethod.PAYSTACK)
                .paymentStatus(PaymentStatus.PENDING)
                .build();
        booking.setBookingPayment(bookingPayment);
        paymentRepository.save(bookingPayment);
    }

    private String generateTransactionRef() {
        return "PSK-" + System.currentTimeMillis();
    }

    @Transactional
    @Override
    public void verifyPaystackWebhook(String payload, String signature) {
        try {
            // Verify signature
            String computedSignature = computeHmacSHA512(payload, secretKey);
            if (!computedSignature.equals(signature)) {
                log.warn("Invalid Paystack webhook signature");
                return;
            }

            JsonNode json = objectMapper.readTree(payload);
            String event = json.get("event").asText();
            if (!"charge.success".equals(event)) {
                log.info("Unhandled event: {}", event);
                return;
            }

            String reference = json.get("data").get("reference").asText();

            BookingPayment payment = paymentRepository.findByTransactionReference(reference);
            if (payment == null) {
                log.warn("No payment found for reference: {}", reference);
                return;
            }

            if (payment.getPaymentStatus() == PaymentStatus.COMPLETED) {
                log.info("Payment already processed for reference: {}", reference);
                return;
            }

            Booking booking = payment.getBooking();
            PaymentSuccessDTO dto = PaymentSuccessDTO.builder()
                    .bookingId(booking.getBookingId())
                    .pnrCode(booking.getBookingNumber())
                    .paymentId(reference)
                    .build();

            jmsTemplate.convertAndSend("payment-queue", objectMapper.writeValueAsString(dto));
            log.info("Paystack payment verified and pushed to queue");

        } catch (Exception e) {
            log.error("Failed to process Paystack webhook", e);
            jmsTemplate.convertAndSend("payment-failure-queue", payload);
        }
    }

    private String computeHmacSHA512(String data, String key) throws Exception {
        Mac sha512Hmac = Mac.getInstance("HmacSHA512");
        SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "HmacSHA512");
        sha512Hmac.init(keySpec);
        byte[] macData = sha512Hmac.doFinal(data.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : macData) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
