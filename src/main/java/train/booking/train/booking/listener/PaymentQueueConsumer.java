package train.booking.train.booking.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import train.booking.train.booking.dto.BookSeatDTO;
import train.booking.train.booking.dto.PaymentSuccessDTO;
import train.booking.train.booking.model.Booking;
import train.booking.train.booking.model.OtherPassenger;
import train.booking.train.booking.model.Seat;
import train.booking.train.booking.model.enums.BookingStatus;
import train.booking.train.booking.service.BookingService;
import train.booking.train.booking.service.OtherPassengerService;
import train.booking.train.booking.service.SeatService;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentQueueConsumer {

    private final BookingService bookingService;
    private final SeatService seatService;
    private final OtherPassengerService otherPassengerService;
    @Transactional
    @JmsListener(destination = "payment-queue")
    public void handlePaymentSuccess(String json) throws JsonProcessingException {
//        try {
            ObjectMapper mapper = new ObjectMapper();
            PaymentSuccessDTO dto = mapper.readValue(json, PaymentSuccessDTO.class);
            Booking booking = bookingService.findBookingById(dto.getBookingId());
            if (booking.getBookingStatus() == BookingStatus.BOOKED) {
                log.info("Booking {} already processed.skipped", booking.getBookingNumber());
                return;

            }
            bookingService.updateBookingStatus(dto.getBookingId());
                BookSeatDTO seatDTO = new BookSeatDTO();
                seatDTO.setTrainClass(booking.getTrainClass());
                seatDTO.setSeatNumber(booking.getSeatNumber());
                seatDTO.setBookingId(booking.getBookingId());
                Seat seat = seatService.bookSeat(seatDTO);
                log.info("Finding seat for seatNumber={}, scheduleId={}, trainClass={}",
                        seat.getSeatNumber(),
                        seat.getScheduleId(),
                        seat.getTrainClass());

                log.info("Booking {} confirmed and seat {} reserved", booking.getBookingNumber(), seat.getSeatNumber());

    List<OtherPassenger>otherPassengers = otherPassengerService.findByBookingId(dto.getBookingId());
    for(OtherPassenger passenger : otherPassengers){
        if(passenger.getSeatNumber() != null && passenger.getSeatNumber() > 0){
            try{
                BookSeatDTO otherSeat = new BookSeatDTO();
                otherSeat.setTrainClass(booking.getTrainClass());
                otherSeat.setSeatNumber(passenger.getSeatNumber());
                otherSeat.setBookingId(booking.getBookingId());
                seatService.bookSeat(otherSeat);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            }

        else {
            log.warn("Other passenger seatNumber missing or invalid: {}", passenger.getName());
        }
    }

            }


@JmsListener(destination = "payment-failure-queue")
    public void handlePaymentFailure (String json){
        log.warn("Received failed payment details for analysis: {}", json);
    }
}

