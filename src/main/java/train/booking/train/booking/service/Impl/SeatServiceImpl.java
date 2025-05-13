package train.booking.train.booking.service.Impl;//package OnlineBookingSystem.OnlineBookingSystem.service.Impl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import train.booking.train.booking.dto.BookSeatDTO;
import train.booking.train.booking.dto.SeatDto;
import train.booking.train.booking.dto.TrainClassAvailabilityDTO;
import train.booking.train.booking.dto.response.BaseResponse;
import train.booking.train.booking.dto.response.ResponseUtil;
import train.booking.train.booking.exceptions.InvalidSeatNumberException;
import train.booking.train.booking.exceptions.SeatAlreadyBookedException;
import train.booking.train.booking.exceptions.SeatCannotBeFoundException;
import train.booking.train.booking.model.Schedule;
import train.booking.train.booking.model.Seat;
import train.booking.train.booking.model.enums.SeatStatus;
import train.booking.train.booking.model.enums.TrainClass;
import train.booking.train.booking.repository.ScheduleRepository;
import train.booking.train.booking.repository.SeatRepository;
import train.booking.train.booking.service.SeatService;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SeatServiceImpl implements SeatService {

private final SeatRepository seatRepository;
private final ScheduleRepository scheduleRepository;

    @Async
    public BaseResponse generateSeats(List<SeatDto> seatDtos) {
        try {
            List<Seat> seats = new ArrayList<>();

            for (SeatDto seatDto : seatDtos) {
                for (int i = seatDto.getStartSeat(); i <= seatDto.getEndSeat(); i++) {
                    Seat seat = new Seat();
                    seat.setSeatNumber(i);
                    seat.setSeatStatus(SeatStatus.AVAILABLE);
                    seat.setTrainClass(seatDto.getTrainClass());
                    seats.add(seat);
                }
            }
            List<Seat> savedSeats = seatRepository.saveAll(seats);

            List<SeatDto> seatDtoList = savedSeats.stream().map(seat -> {
                SeatDto dto = new SeatDto();
                dto.setSeatNumber(seat.getSeatNumber());
                dto.setStatus(seat.getSeatStatus());
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
    public Seat findSeatBySeatNumber(int seatNumber) {
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

    public Seat bookSeat(BookSeatDTO bookSeatDTO) {
        try {

            Seat foundSeat = seatRepository.findBySeatNumberAndTrainClass(bookSeatDTO.getSeatNumber(), bookSeatDTO.getTrainClass());
            if (foundSeat == null) {
                throw new InvalidSeatNumberException("Seat Number cannot be found");

            }
            if (foundSeat.getSeatStatus() == SeatStatus.BOOKED) {
                throw new SeatAlreadyBookedException("Seat Number : " + bookSeatDTO.getSeatNumber() + "ia already booked");
            }
            foundSeat.setSeatStatus(SeatStatus.BOOKED);
            return seatRepository.save(foundSeat);

        }
        catch (Exception e){
            log.error("Error booking seats", e);
            throw new InvalidSeatNumberException("Error booking Seat");
        }

    }

    @Override
    public Page<Seat> findAllSeat(int page, int size) {
        Page foundSeat = seatRepository.findAll(PageRequest.of(page, size));
        return foundSeat;
    }



}
