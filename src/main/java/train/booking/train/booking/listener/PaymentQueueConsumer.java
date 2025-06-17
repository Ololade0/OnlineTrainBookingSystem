package train.booking.train.booking.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import train.booking.train.booking.dto.BookSeatDTO;
import train.booking.train.booking.dto.PaymentSuccessDTO;
import train.booking.train.booking.model.Booking;
import train.booking.train.booking.model.Seat;
import train.booking.train.booking.service.BookingService;
import train.booking.train.booking.service.SeatService;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentQueueConsumer {

    private final BookingService bookingService;
    private final SeatService seatService;

    @JmsListener(destination = "payment-queue")
    public void handlePaymentSuccess(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            PaymentSuccessDTO dto = mapper.readValue(json, PaymentSuccessDTO.class);

           Booking booking = bookingService.findBookingById(dto.getBookingId());
            bookingService.updateBookingStatus(dto.getBookingId()); // or update

            BookSeatDTO seatDTO = new BookSeatDTO();
            seatDTO.setTrainClass(booking.getTrainClass());
            seatDTO.setSeatNumber(booking.getSeatNumber());

            Seat seat = seatService.bookSeat(seatDTO);
            log.info("Booking {} confirmed and seat {} reserved", booking.getBookingNumber(), seat.getSeatNumber());

        } catch (Exception e) {
            log.error("Failed to process payment success event", e);
            // Optionally send to a dead-letter queue (DLQ)
        }
    }
}

