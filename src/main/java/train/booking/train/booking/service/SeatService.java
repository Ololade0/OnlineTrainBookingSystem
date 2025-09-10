package train.booking.train.booking.service;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PathVariable;
import train.booking.train.booking.dto.BookSeatDTO;
import train.booking.train.booking.dto.GenerateSeatDto;
import train.booking.train.booking.dto.response.BaseResponse;
import train.booking.train.booking.model.Booking;
import train.booking.train.booking.model.Seat;
import train.booking.train.booking.model.enums.TrainClass;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface SeatService {
 BaseResponse generateSeats(List<GenerateSeatDto> seatDtos, Long  trainId);
String   lockSeatTemporarilyForPayment(int seatNumber, Long scheduleId, TrainClass trainClass, Booking bookingId);
  void releaseLockedSeatAfterExpiration();
    void checkSeatAvailability(int seatNumber, Long scheduleId, TrainClass trainClass);

  Seat bookSeat(BookSeatDTO bookSeatDTO);
 Page<Seat> findAllSeat(int page, int size);
Seat findSeatBySeatNumber(int seatNumber);
Optional<Seat> findSeatById(Long seatId);

    List<Map<String, Object>> getSeatSummary(Long trainId);
    void updateSeat(Seat bookedSeat);
}