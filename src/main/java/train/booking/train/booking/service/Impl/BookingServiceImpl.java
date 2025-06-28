package train.booking.train.booking.service.Impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import train.booking.train.booking.dto.BookingQueueDTO;
import train.booking.train.booking.dto.BookingRequestDTO;
import train.booking.train.booking.dto.BookingResponse;
import train.booking.train.booking.dto.PriceListDTO;
import train.booking.train.booking.dto.response.ScheduleResponse;
import train.booking.train.booking.exceptions.BookingCannotBeFoundException;
import train.booking.train.booking.model.Booking;
import train.booking.train.booking.model.OtherPassenger;
import train.booking.train.booking.model.Schedule;
import train.booking.train.booking.model.User;
import train.booking.train.booking.model.enums.AgeRange;
import train.booking.train.booking.model.enums.BookingStatus;
import train.booking.train.booking.model.enums.TrainClass;
import train.booking.train.booking.repository.BookingRepository;
import train.booking.train.booking.service.*;
import train.booking.train.booking.utils.PnrCodeGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
        private final BookingRepository bookingRepository;
        private final UserService userService;
        private final ScheduleService scheduleService;
        private final SeatService seatService;
        private final PnrCodeGenerator pnrCodeGenerator;
        private final JmsTemplate jmsTemplate;
        private final ObjectMapper objectMapper;
//        private final OtherPassengerService otherPassengerService;

@Transactional
@Override
    public BookingResponse createBooking(BookingRequestDTO bookingDTO) {
        User user = userService.findUserById(bookingDTO.getUserId());
        ScheduleResponse scheduleResponse = scheduleService.findSchedule(
                bookingDTO.getDepartureStationId(),
                bookingDTO.getArrivalStationId(),
                bookingDTO.getDepartureDate()
        );
        seatService.checkSeatAvailability(bookingDTO.getSeatNumber(), scheduleResponse.getScheduleId(), bookingDTO.getTrainClass());
   BigDecimal totalFare = calculateTotalFare(bookingDTO, scheduleResponse);
    String pnrCode = pnrCodeGenerator.generateUniquePnrCodes();
        BookingQueueDTO bookingQueueDTO = buildBookingQueueDTO(bookingDTO, user.getId(), scheduleResponse, pnrCode, totalFare);
        try {
            String jsonPayload = objectMapper.writeValueAsString(bookingQueueDTO);
            jmsTemplate.convertAndSend("bookingQueue", jsonPayload);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize BookingQueueDTO", e);
        }

        return BookingResponse.builder()
                .pnrCode(pnrCode)
                .totalFare(totalFare)
                .message("Your seat has been reserved for 10 minutes.")
                .build();

    }

    private BigDecimal calculateTotalFare(BookingRequestDTO bookingDTO, ScheduleResponse scheduleResponse) {
        BigDecimal convenienceCharge = BigDecimal.valueOf(200);
        BigDecimal totalFare = BigDecimal.ZERO;

        BigDecimal mainFare = extractFare(scheduleResponse, bookingDTO.getTrainClass(), bookingDTO.getPassengerType());
        totalFare = totalFare.add(mainFare).add(convenienceCharge);

        if (bookingDTO.getAdditionalPassenger() != null) {
            for (OtherPassenger additional : bookingDTO.getAdditionalPassenger()) {
                BigDecimal additionalFare = extractFare(scheduleResponse, bookingDTO.getTrainClass(), additional.getPassengerType());
                totalFare = totalFare.add(additionalFare).add(convenienceCharge);
            }
        }

        return totalFare;
    }



    private BigDecimal extractFare(ScheduleResponse scheduleResponse, TrainClass trainClass, AgeRange ageRange) {
        return scheduleResponse.getSchedules().stream()
                .filter(schedule -> schedule.getScheduleId().equals(scheduleResponse.getScheduleId()))
                .flatMap(schedule -> schedule.getPrices().stream())
                .filter(price -> price.getTrainClass().equals(trainClass)
                        && price.getAgeRange().equals(ageRange))
                .map(PriceListDTO::getPrice)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No price found for selected train class and age range"));
    }

    private BookingQueueDTO buildBookingQueueDTO(
            BookingRequestDTO bookingDTO,
            Long userId,
            ScheduleResponse scheduleResponse,
            String bookingNumber,
            BigDecimal totalFare
    ) {
        return BookingQueueDTO.builder()
                .userId(userId)
                .scheduleId(scheduleResponse.getScheduleId())
                .travelDate(scheduleResponse.getDepartureDate())
                .travelTime(scheduleResponse.getDepartureTime())
                .trainClass(bookingDTO.getTrainClass())
                .seatNumber(bookingDTO.getSeatNumber())
                .bookingNumber(bookingNumber)
                .passengerType(bookingDTO.getPassengerType())
                .bookingRequestDTO(bookingDTO)
                .totalFare(totalFare)
                .build();
    }

    public Booking saveBooking(BookingQueueDTO dto) {
        User user = userService.findUserById(dto.getUserId());
        Schedule schedule = scheduleService.findSchedulesById(dto.getScheduleId());
        Booking booking = Booking.builder()
                .bookingDate(LocalDateTime.now())
                .user(user)
                .scheduleId(schedule.getId())
                .passengerType(dto.getPassengerType())
                .travelDate(schedule.getDepartureDate())
                .travelTime(schedule.getDepartureTime())
                .trainClass(dto.getTrainClass())
                .seatNumber(dto.getSeatNumber())
                .bookingStatus(BookingStatus.RESERVED)
                .totalFareAmount(dto.getTotalFare())
                .bookingNumber(dto.getBookingNumber())
                .build();

        return bookingRepository.save(booking);
    }



    @Override
        public Booking findBookingById(Long bookingId) {
          return bookingRepository.findById(bookingId).orElseThrow(()->
               new BookingCannotBeFoundException("Booking with Id" + bookingId + " cannot be found ")

          );
        }
    @Override
        public Optional<Booking> findBookingByBookingNumber(String bookingNumber){
   Booking foundBooking = bookingRepository.findByBookingNumber(bookingNumber);
   if(foundBooking == null){
       throw new BookingCannotBeFoundException("Booking with pnr " + bookingNumber + "cannot be found");
   }
   return Optional.of(foundBooking);
        }


        @Override
        public Booking findByTransactionId(String transactionId) {

    return null;
        }


    @Override
    public Booking updateBookingStatus(Long bookingId) {
        Booking booking = findBookingById(bookingId);
        booking.setBookingStatus(BookingStatus.BOOKED);
        return bookingRepository.save(booking);
    }




}





