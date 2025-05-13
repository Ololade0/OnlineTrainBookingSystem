package train.booking.train.booking.service.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import train.booking.train.booking.dto.BookingDTO;
import train.booking.train.booking.model.Booking;
import train.booking.train.booking.model.Schedule;
import train.booking.train.booking.model.User;
import train.booking.train.booking.model.enums.BookingStatus;
import train.booking.train.booking.repository.BookingRepository;
import train.booking.train.booking.service.BookingService;
import train.booking.train.booking.service.ScheduleService;
import train.booking.train.booking.service.TrainService;
import train.booking.train.booking.service.UserService;
import train.booking.train.booking.utils.PnrCodeGenerator;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;

    private final UserService userService;
    private final ScheduleService scheduleService;
    private final TrainService trainService;
    private final PnrCodeGenerator pnrCodeGenerator;


    @Override
    public Booking createBooking(BookingDTO bookingDTO) {
        User foundUser = userService.findUserById(bookingDTO.getUserId());
      Schedule foundSchedule =  scheduleService.findSchedulesById(bookingDTO.getScheduleId());
        if(foundSchedule != null){
            Booking createBooking = Booking.builder()
                    .bookingDate(LocalDateTime.now())
                    .user(foundUser)
                    .scheduleId(foundSchedule.getId())
                    .travelDate(foundSchedule.getDepartureDate())
                    .trainClass(bookingDTO.getTrainClass())
                    .seatNumber(bookingDTO.getSeatNumber())
                    .bookingStatus(BookingStatus.PENDING)
                    .passengerNameRecord(pnrCodeGenerator.generateUniquePnrCodes())
                    .build();
          return   bookingRepository.save(createBooking);
        }

        return null;



        }




}
