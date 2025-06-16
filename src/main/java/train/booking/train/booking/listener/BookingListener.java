package train.booking.train.booking.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.jms.Message;
import jakarta.jms.TextMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import train.booking.train.booking.dto.BookingQueueDTO;
import train.booking.train.booking.service.BookingService;

@Slf4j
@Component
@RequiredArgsConstructor
public class BookingListener {

    private final ObjectMapper objectMapper;
    private final BookingService bookingService;



        @JmsListener(destination = "bookingQueue")
        public void receiveBookingMessage(Message message) {
            try {
                if (message instanceof TextMessage textMessage) {
                    String payload = textMessage.getText();
                    log.info("Received booking message: {}", payload);

                    BookingQueueDTO dto = objectMapper.readValue(payload, BookingQueueDTO.class);
                    bookingService.saveBooking(dto);

                    log.info("Booking successfully saved for PNR: {}", dto.getBookingNumber());
                } else {
                    log.error("Unsupported message type: {}", message.getClass().getSimpleName());
                }
            } catch (Exception e) {
                log.error("Failed to process booking message", e);
            }
        }
    }


