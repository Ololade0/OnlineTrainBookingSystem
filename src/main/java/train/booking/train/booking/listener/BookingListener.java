package train.booking.train.booking.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.TextMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import train.booking.train.booking.dto.BookingQueueDTO;
import train.booking.train.booking.exceptions.SeatAlreadyReservedException;
import train.booking.train.booking.model.Booking;
import train.booking.train.booking.model.OtherPassenger;
import train.booking.train.booking.service.BookingService;
import train.booking.train.booking.service.OtherPassengerService;
import train.booking.train.booking.service.SeatService;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class BookingListener {

    private final ObjectMapper objectMapper;
    private final BookingService bookingService;
    private final SeatService seatService;
    private final OtherPassengerService otherPassengerService;

    @Transactional
    @JmsListener(destination = "bookingQueue")
    public void receiveBookingMessage(Message message) throws JsonProcessingException, JMSException {


            if (message instanceof TextMessage textMessage) {
                String payload = textMessage.getText();
                log.info("Received booking message: {}", payload);

                BookingQueueDTO dto = objectMapper.readValue(payload, BookingQueueDTO.class);
                processBooking(dto);
            } else {
                log.error("Unsupported message type: {}", message.getClass().getSimpleName());
            }


    }

    @Transactional(rollbackFor = Exception.class)
    public void processBooking(BookingQueueDTO dto) {
        Booking savedBooking = bookingService.saveBooking(dto);
        seatService.lockSeatTemporarilyForPayment(
                dto.getSeatNumber(),
                dto.getScheduleId(),
                dto.getTrainClass(),
                savedBooking
        );

        // 3. Save other passengers
        List<OtherPassenger> savedOtherPassengers = otherPassengerService
                .addNewPassenger(dto.getBookingRequestDTO(), dto.getUserId(), savedBooking);

        // 4. Lock seat for each other passenger (if they have seat numbers)
        if (savedOtherPassengers != null && !savedOtherPassengers.isEmpty()) {
            for (OtherPassenger passenger : savedOtherPassengers) {
                if (passenger.getSeatNumber() != null && passenger.getSeatNumber() > 0) {
                    try {
                        seatService.lockSeatTemporarilyForPayment(
                                passenger.getSeatNumber(),
                                dto.getScheduleId(),
                                dto.getTrainClass(),
                                savedBooking
                        );
                    } catch (SeatAlreadyReservedException e) {
                        log.error("Seat Already been booked or Reserved by another User", e);
                    }

                } else {
                    log.warn("Other passenger seatNumber is missing or invalid for: {}", passenger.getName());
                }
            }

            log.info("Booking successfully saved and seats locked for PNR: {}", dto.getBookingNumber());
        }


    }

}
