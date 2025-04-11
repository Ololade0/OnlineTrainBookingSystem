package train.booking.train.booking.service.Impl;//package OnlineBookingSystem.OnlineBookingSystem.service.Impl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import train.booking.train.booking.dto.SeatDto;
import train.booking.train.booking.dto.response.BaseResponse;
import train.booking.train.booking.dto.response.ResponseUtil;
import train.booking.train.booking.exceptions.SeatCannotBeFoundException;
import train.booking.train.booking.model.Seat;
import train.booking.train.booking.model.enums.SeatStatus;
import train.booking.train.booking.repository.SeatRepository;
import train.booking.train.booking.service.SeatService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SeatServiceImpl implements SeatService {

private final SeatRepository seatRepository;


    //    @Override
//    public Seat bookSeat(String trainClassName, int seatNumber) {
//        Optional<Seat> foundSeat = seatRepository.findByTrainClass_TrainClassNameAndSeatNumber(trainClassName, seatNumber);
//
//        if (foundSeat.isEmpty()){
//            throw new InvalidSeatNumberException("Seat number " + seatNumber + " does not exist.");
//
//        }
//        if (foundSeat.get().getStatus() == SeatStatus.BOOKED) {
//            throw new SeatAlreadyBookedException("Seat number " + seatNumber + " has already been booked");
//        }
//        Seat seatToBook = foundSeat.get();
//        seatToBook.setStatus(SeatStatus.BOOKED);
//        return seatRepository.save(seatToBook);
//    }



    @Async
    public BaseResponse generateSeats(List<SeatDto> seatDtos) {
        try {


            List<Seat> seats = new ArrayList<>();

            for (SeatDto seatDto : seatDtos) {
                for (int i = seatDto.getStartSeat(); i <= seatDto.getEndSeat(); i++) {
                    Seat seat = new Seat();
                    seat.setSeatNumber(i);
                    seat.setStatus(SeatStatus.AVAILABLE);
                    seat.setTrainClass(seatDto.getTrainClass());
                    seats.add(seat);
                }
            }

            List<Seat> savedSeats = seatRepository.saveAll(seats);

            List<SeatDto> seatDtoList = savedSeats.stream().map(seat -> {
                SeatDto dto = new SeatDto();
                dto.setSeatNumber(seat.getSeatNumber());
                dto.setStatus(seat.getStatus());
                dto.setTrainClass(seat.getTrainClass());
                return dto;
            }).toList();
            return ResponseUtil.success("Seats have been successfully generated", savedSeats);

        } catch (Exception e) {
            log.error("Error during processing: {}", e.getMessage());
            return ResponseUtil.invalidOrNullInput("Error generating seat.");
        }
    }





    @Override
    public Seat findSeat(int seatNumber) {
        return seatRepository.findBySeatNumber(seatNumber).orElseThrow(()->
                new SeatCannotBeFoundException("Seat with number" + seatNumber + "cannot be found"));
    }

    @Override
    public Optional<Seat> findSeatById(Long seatId) {
        return Optional.ofNullable(seatRepository.findById(seatId).orElseThrow(() ->
                new SeatCannotBeFoundException("Seat cannot be found")));
    }



    public void updateSeat(Seat seat) {
        seatRepository.save(seat);
    }

    @Override
    public List<Seat> findAllSeat() {
        return seatRepository.findAll();
    }
//
//    public Seat findSeats(String className, int seatNumber) {
//        return seatRepository.findByTrainClass_TrainClassNameAndSeatNumber(className, seatNumber)
//                .orElseThrow(() -> new SeatCannotBeFoundException("Seat not found for class: " + className + " and seat number: " + seatNumber));
//    }




}
