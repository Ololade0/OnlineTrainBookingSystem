package train.booking.train.booking.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.jms.Message;
import jakarta.jms.TextMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import train.booking.train.booking.dto.BookingQueueDTO;
import train.booking.train.booking.model.Booking;
import train.booking.train.booking.model.Schedule;
import train.booking.train.booking.model.User;
import train.booking.train.booking.model.enums.BookingStatus;
import train.booking.train.booking.repository.BookingRepository;
import train.booking.train.booking.service.ScheduleService;
import train.booking.train.booking.service.UserService;

@Slf4j
@Component
@RequiredArgsConstructor
public class BookingQueueConsumer {

    private final ObjectMapper objectMapper;
    private final UserService userService;
    private final ScheduleService scheduleService;
    private final BookingRepository bookingRepository;

    @JmsListener(destination = "bookingQueue")
    public void receiveBookingMessage(Message message) {
        try {
            if (message instanceof TextMessage textMessage) {
                String payload = textMessage.getText();
                log.info("Received booking message: {}", payload);

                BookingQueueDTO dto = objectMapper.readValue(payload, BookingQueueDTO.class);

                User user = userService.findUserById(dto.getUserId());
                Schedule schedule = scheduleService.findSchedulesById(dto.getScheduleId());

                if (user == null || schedule == null) {
                    log.error("User or Schedule not found for booking: {}", dto);
                    return;
                }

                Booking booking = Booking.builder()
                        .user(user)
                        .scheduleId(schedule.getId())
                        .travelDate(dto.getTravelDate())
                        .trainClass(dto.getTrainClass())
                        .seatNumber(dto.getSeatNumber())
                        .bookingStatus(BookingStatus.PENDING)
                        .bookingNameRecord(dto.getBookingNameRecord())
                        .build();

                bookingRepository.save(booking);
                log.info("Booking saved successfully: {}", booking.getBookingNameRecord());
            } else {
                log.error("Unsupported message type received: {}", message.getClass().getSimpleName());
            }
        } catch (Exception e) {
            log.error("Error processing booking message", e);
        }
    }
}
