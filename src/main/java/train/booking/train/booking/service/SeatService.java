package train.booking.train.booking.service;
import train.booking.train.booking.dto.SeatDto;
import train.booking.train.booking.dto.response.BaseResponse;
import train.booking.train.booking.model.Seat;
import train.booking.train.booking.model.TrainClass;

import java.util.List;
import java.util.Optional;

public interface SeatService {
     BaseResponse generateSeats(List<SeatDto> seatDtos);
Seat findSeat(int seatNumber);
Optional<Seat> findSeatById(Long seatId);
void updateSeat(Seat seat);
List<Seat> findAllSeat();

//  Seat findSeats(String className, int seatNumber);



}