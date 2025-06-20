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
import train.booking.train.booking.model.enums.BookingStatus;
import train.booking.train.booking.service.BookingService;
import train.booking.train.booking.service.ScheduleService;
import train.booking.train.booking.service.SeatService;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentQueueConsumer {

    private final BookingService bookingService;
    private final SeatService seatService;
    private final ScheduleService scheduleService;

    @JmsListener(destination = "payment-queue")
    public void handlePaymentSuccess(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            PaymentSuccessDTO dto = mapper.readValue(json, PaymentSuccessDTO.class);
           Booking booking = bookingService.findBookingById(dto.getBookingId());
//            Schedule foundSchedule = scheduleService.findSchedulesById(dto.getScheduleId());
           if(booking.getBookingStatus() == BookingStatus.BOOKED){
               log.info("Booking {} already processed.skipped", booking.getBookingNumber());
               return;

           }
            bookingService.updateBookingStatus(dto.getBookingId());
            BookSeatDTO seatDTO = new BookSeatDTO();
            seatDTO.setTrainClass(booking.getTrainClass());
            seatDTO.setSeatNumber(booking.getSeatNumber());
            seatDTO.setBookingId(booking.getBookingId());
            Seat seat = seatService.bookSeat(seatDTO);
            log.info("Booking {} confirmed and seat {} reserved", booking.getBookingNumber(), seat.getSeatNumber());

        } catch (Exception e) {
            log.error("Failed to process payment success event", e);
        }
    }

@JmsListener(destination = "payment-failure-queue")
    public void handlePaymentFailure (String json){
        log.warn("Received failed payment details for analysis: {}", json);
    }
}

