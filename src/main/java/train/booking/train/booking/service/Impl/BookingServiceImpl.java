package train.booking.train.booking.service.Impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import train.booking.train.booking.dto.BookingQueueDTO;
import train.booking.train.booking.dto.BookingRequestDTO;
import train.booking.train.booking.dto.PaymentRequest;
import train.booking.train.booking.exceptions.BookingCannotBeFoundException;
import train.booking.train.booking.exceptions.ScheduleCannotBeFoundException;
import train.booking.train.booking.model.Booking;
import train.booking.train.booking.model.Schedule;
import train.booking.train.booking.model.User;
import train.booking.train.booking.model.enums.BookingStatus;
import train.booking.train.booking.repository.BookingRepository;
import train.booking.train.booking.service.BookingService;
import train.booking.train.booking.service.ScheduleService;
import train.booking.train.booking.service.UserService;
import train.booking.train.booking.utils.PnrCodeGenerator;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
        private final BookingRepository bookingRepository;
        private final UserService userService;
        private final ScheduleService scheduleService;
          private final PnrCodeGenerator pnrCodeGenerator;

        private final JmsTemplate jmsTemplate;
        private final ObjectMapper objectMapper;


    public void createBooking(BookingRequestDTO bookingDTO) {
        if (bookingDTO.getUserId() == null || bookingDTO.getScheduleId() == null) {
            throw new IllegalArgumentException("User ID and Schedule ID must not be null");
        }

        User foundUser = userService.findUserById(bookingDTO.getUserId());
        Schedule foundSchedule = scheduleService.findSchedulesById(bookingDTO.getScheduleId());

        if (foundSchedule == null) {
            throw new ScheduleCannotBeFoundException("Schedule cannot be found");
        }

        String pnrCode = pnrCodeGenerator.generateUniquePnrCodes();

        BookingQueueDTO queueDTO = BookingQueueDTO.builder()
                .userId(foundUser.getId())
                .scheduleId(foundSchedule.getId())
                .travelDate(foundSchedule.getDepartureDate())
                .trainClass(bookingDTO.getTrainClass())
                .seatNumber(bookingDTO.getSeatNumber())
                .bookingNameRecord(pnrCode)
                .build();

        try {
            String jsonPayload = objectMapper.writeValueAsString(queueDTO);
            jmsTemplate.convertAndSend("bookingQueue", jsonPayload);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize BookingQueueDTO", e);
        }
    }

//    @Override
//    public void createBooking(BookingDTO bookingDTO) {
//        User foundUser = userService.findUserById(bookingDTO.getUserId());
//        Schedule foundSchedule = scheduleService.findSchedulesById(bookingDTO.getScheduleId());
//
//        if (foundSchedule == null) {
//            throw new ScheduleCannotBeFoundException("Schedule cannot be found");
//        }
//
//        bookingDTO.setTravelDate(foundSchedule.getDepartureDate());
//        bookingDTO.setBookingNameRecord(pnrCodeGenerator.generateUniquePnrCodes());
//
//        jmsTemplate.convertAndSend("bookingQueue", bookingDTO);
//    }



    //    @Override
        public void bookingWithPayment(Long bookingId, PaymentRequest paymentRequest) {
          Optional<Booking> reservedBooking  = bookingRepository.findById(bookingId);
          paymentRequest.setBookingId(reservedBooking.get().getBookingId());
            jmsTemplate.convertAndSend("payment-queue", paymentRequest);
        }


    public Booking updateBookingStatus(Long bookingId, BookingStatus status) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingCannotBeFoundException("Booking not found with ID: " + bookingId));

        booking.setBookingStatus(status);
        return bookingRepository.save(booking);
    }






    @Override
        public Booking findBookingById(Long bookingId) {
          return bookingRepository.findById(bookingId).orElseThrow(()->
               new BookingCannotBeFoundException("Booking with Id" + bookingId + " cannot be found ")

          );
        }

        @Override
        public Booking saveBooking(Booking bookingDTO) {

//            User foundUser = userService.findUserById(bookingDTO.get);
            existsByBookingNameRecord(bookingDTO.getBookingNameRecord());
            Booking booking = new Booking();
            booking.setBookingDate(bookingDTO.getBookingDate());
//           booking.setUser(foundUser);
            booking.setScheduleId(bookingDTO.getScheduleId());
            booking.setTravelDate(bookingDTO.getTravelDate());
            booking.setTrainClass(bookingDTO.getTrainClass());
            booking.setSeatNumber(bookingDTO.getSeatNumber());
            booking.setBookingStatus(BookingStatus.PENDING);
            booking.setBookingNameRecord(booking.getBookingNameRecord());

            // Save the Booking entity to the repository
            Booking savedBooking = bookingRepository.save(booking);

            // Convert saved Booking entity back to BookingDTO
//            BookingDTO savedBookingDTO = new BookingDTO();
//            savedBookingDTO.setBookingId(savedBooking.getBookingId());
//            savedBookingDTO.setUserId(savedBooking.getUser().getId());
//            savedBookingDTO.setScheduleId(savedBooking.getScheduleId());
//            savedBookingDTO.setTravelDate(savedBooking.getTravelDate());
//            savedBookingDTO.setTrainClass(savedBooking.getTrainClass());
//            savedBookingDTO.setSeatNumber(savedBooking.getSeatNumber());
//            savedBookingDTO.setBookingStatus(savedBooking.getBookingStatus());
//            savedBookingDTO.setBookingNameRecord(savedBooking.getBookingNameRecord());
            return savedBooking;
        }

        @Override
        public boolean existsByBookingNameRecord(String bookingNameRecord) {
            if (bookingNameRecord == null) {
                return false;
            }
            return bookingRepository.existsByBookingNameRecord(bookingNameRecord);
        }

        @Override
        public Booking findByTransactionId(String transactionId) {
            return null;
        }





    //    public void sendPaymentRequest(PaymentRequest paymentRequestDTO) {
    //        jmsTemplate.convertAndSend("payment-queue", paymentRequestDTO);
    //    }


    //
    //    @Override
    //    public Booking bookingWithPayment(Long bookingId, PaymentRequest paymentRequest) {
    //      Optional<Booking> reservedBooking  = bookingRepository.findById(bookingId);
    //      paymentRequest.setBookingId(reservedBooking.get().getBookingId());
    //      paymentService.sendPaymentMessage(paymentRequest);
    //      return reservedBooking.get();
    //    }


}





