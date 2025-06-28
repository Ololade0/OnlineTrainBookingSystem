package train.booking.train.booking.service;

import org.springframework.data.domain.Page;
import train.booking.train.booking.dto.BookSeatDTO;
import train.booking.train.booking.dto.SeatDto;
import train.booking.train.booking.dto.response.BaseResponse;
import train.booking.train.booking.model.Booking;
import train.booking.train.booking.model.Seat;
import train.booking.train.booking.model.enums.TrainClass;

import java.util.List;
import java.util.Optional;

public interface SeatService {
 BaseResponse generateSeats(List<SeatDto> seatDtos, Long scheduleId);
String   lockSeatTemporarilyForPayment(int seatNumber, Long scheduleId, TrainClass trainClass, Booking bookingId);
  void releaseLockedSeatAfterExpiration();
    void checkSeatAvailability(int seatNumber, Long scheduleId, TrainClass trainClass);

  Seat bookSeat(BookSeatDTO bookSeatDTO);
 Page<Seat> findAllSeat(int page, int size);
Seat findSeatBySeatNumber(int seatNumber);
Optional<Seat> findSeatById(Long seatId);


    void updateSeat(Seat bookedSeat);
}