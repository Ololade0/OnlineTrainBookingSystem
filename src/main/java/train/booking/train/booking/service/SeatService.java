package train.booking.train.booking.service;

import train.booking.train.booking.model.Seat;
import train.booking.train.booking.model.TrainClass;

import java.util.List;
import java.util.Optional;

public interface SeatService {

  List<Seat> generateSeats(int startSeat, int endSeat, TrainClass trainClass);

  Seat bookSeat(String trainClassName, int seatNumber);
  Seat findSeat(int seatNumber);
  Seat findSeatById(Long seatId);
  void updateSeat(Seat seat);
  List<Seat> findAllSeat();

  Seat findSeatByClassAndNumber(String className, int seatNumber);

}