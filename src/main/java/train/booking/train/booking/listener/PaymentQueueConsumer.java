package train.booking.train.booking.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import train.booking.train.booking.WebSocket.WebSocketImpl;
import train.booking.train.booking.dto.BookSeatDTO;
import train.booking.train.booking.dto.BookingTicketDTO;
import train.booking.train.booking.dto.PaymentSuccessDTO;
import train.booking.train.booking.model.Booking;
import train.booking.train.booking.model.BookingPayment;
import train.booking.train.booking.model.Seat;
import train.booking.train.booking.model.enums.BookingStatus;
import train.booking.train.booking.service.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentQueueConsumer {

    private final BookingService bookingService;
    private final SeatService seatService;
    private final OtherPassengerService otherPassengerService;


    private final PaymentService paymentService;

    private final NotificationService notificationService;

    private final WebSocketImpl webSocket;

    private final ObjectMapper mapper = new ObjectMapper();

    @Transactional
    @JmsListener(destination = "payment-queue")
    public void handlePaymentSuccess(String json) throws Exception {
        PaymentSuccessDTO dto = mapper.readValue(json, PaymentSuccessDTO.class);
        Booking booking = bookingService.findBookingById(dto.getBookingId());

        if (booking.getBookingStatus() == BookingStatus.BOOKED) {
            log.info("Booking {} already processed", booking.getBookingNumber());
            return;
        }

        // Update Booking Status
        bookingService.updateBookingStatus(dto.getBookingId());

        // Book main user seat
        BookSeatDTO seatDto = new BookSeatDTO();
        seatDto.setTrainClass(booking.getTrainClass());
        seatDto.setSeatNumber(booking.getSeatNumber());
        seatDto.setBookingId(booking.getBookingId());
        Seat seat = seatService.bookSeat(seatDto);


        log.info("Seat {} booked for {}", seat.getSeatNumber(), booking.getBookingNumber());
        seatDto.setAvailable(false);
        webSocket.sendSeatUpdate(seatDto);


        // Book seats for other passengers
        otherPassengerService.bookSeatForOtherPassengers(dto, booking);

        // Finally, mark the payment as COMPLETED
        BookingPayment bookingPayment =   paymentService.updateBookingPayment(dto.getPaymentId());
       BookingTicketDTO generateReceipt =  bookingService.generateBookingReceipt(booking.getBookingId());
      notificationService.sendBookingReceipts(bookingPayment.getUser().getEmail(),generateReceipt);
            log.info("Marked payment {} as COMPLETED for booking {}", dto.getPaymentId(), dto.getBookingId());
        }




    @JmsListener(destination = "payment-failure-queue")
    public void handlePaymentFailure(String json) {
        log.warn("Received failed payment details for analysis: {}", json);
        // Optionally store or alert devops team
    }
}
