package train.booking.train.booking.service.Impl;//package OnlineBookingSystem.OnlineBookingSystem.service.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import train.booking.train.booking.dto.BookSeatDTO;
import train.booking.train.booking.dto.SeatDto;
import train.booking.train.booking.dto.response.BaseResponse;
import train.booking.train.booking.dto.response.ResponseUtil;
import train.booking.train.booking.exceptions.*;
import train.booking.train.booking.model.Schedule;
import train.booking.train.booking.model.Seat;
import train.booking.train.booking.model.enums.SeatStatus;
import train.booking.train.booking.model.enums.TrainClass;
import train.booking.train.booking.repository.SeatRepository;
import train.booking.train.booking.service.ScheduleService;
import train.booking.train.booking.service.SeatService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SeatServiceImpl implements SeatService {

private final SeatRepository seatRepository;

private final ScheduleService scheduleService;


    @Async
    public BaseResponse generateSeats(List<SeatDto> seatDtos, Long scheduleId) {
       Schedule foundSchedule = scheduleService.findSchedulesById(scheduleId);
        if(foundSchedule == null){
            throw new ScheduleCannotBeFoundException("Schedule cannot be found");
        }
        try {
            List<Seat> seats = new ArrayList<>();

            for (SeatDto seatDto : seatDtos) {
                for (int i = seatDto.getStartSeat(); i <= seatDto.getEndSeat(); i++) {
                    Seat seat = new Seat();
                    seat.setSeatNumber(i);
                    seat.setSeatStatus(SeatStatus.AVAILABLE);
                    seat.setTrainClass(seatDto.getTrainClass());
                    seat.setScheduleId(foundSchedule.getId());
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
    public String lockSeatTemporarilyForPayment(int seatNumber, Long scheduleId, TrainClass trainClass) {
        Seat seat = seatRepository.findBySeatNumberAndScheduleIdAndTrainClass(seatNumber, scheduleId, trainClass)
                .orElseThrow(() -> new SeatCannotBeFoundException("Seat not found for this schedule"));
        seat.setSeatStatus(SeatStatus.RESERVED);
        seat.setLockTime(LocalDateTime.now());
        seatRepository.save(seat);
        return "Seat has been temporarily reserved for 10 minutes. Please proceed to payment.";

    }
    @Override
    public void checkSeatAvailability(int seatNumber, Long scheduleId, TrainClass trainClass) {
    Seat seat = seatRepository.findBySeatNumberAndScheduleIdAndTrainClass(seatNumber, scheduleId, trainClass)
            .orElseThrow(() -> new SeatCannotBeFoundException("Seat not found for this schedule"));

    if (seat.getSeatStatus() == SeatStatus.BOOKED) {
        throw new SeatAlreadyBookedException("Seat is already booked");
    }

    if (seat.getSeatStatus() == SeatStatus.RESERVED && seat.getLockTime() != null &&
            seat.getLockTime().isAfter(LocalDateTime.now().minusMinutes(10))) {
        throw new SeatAlreadyReservedException("Seat is temporarily reserved. Try again later");
    }

}
    @Scheduled(fixedRate = 60000)
    public void releaseLockedSeatAfterExpiration() {
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(10);
        List<Seat> expiredSeats = seatRepository.findBySeatStatusAndLockTimeBefore(SeatStatus.RESERVED, threshold);

        for (Seat seat : expiredSeats) {
            seat.setSeatStatus(SeatStatus.AVAILABLE);
            seat.setLockTime(null);
        }

        if (!expiredSeats.isEmpty()) {
            seatRepository.saveAll(expiredSeats);
            log.info("Released {} expired seats.", expiredSeats.size());
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



@Transactional
    public Seat bookSeat(BookSeatDTO bookSeatDTO) {
//        b
        try {
            Seat foundSeat = seatRepository.findBySeatNumberAndTrainClass(bookSeatDTO.getSeatNumber(), bookSeatDTO.getTrainClass());
            if (foundSeat == null) {
                throw new InvalidSeatNumberException("Seat Number cannot be found");

            }
            if (foundSeat.getSeatStatus() == SeatStatus.BOOKED) {
                throw new SeatAlreadyBookedException("Seat Number : " + bookSeatDTO.getSeatNumber() + "is already booked");
            }

            if (foundSeat.getSeatStatus() != SeatStatus.RESERVED) {
                throw new SeatAlreadyReservedException("Seat is not available for booking");
            }

//            if (foundSeat.getSeatStatus() == SeatStatus.RESERVED &&
//                    foundSeat.getLockTime() != null &&
//                    foundSeat.getLockTime().isAfter(LocalDateTime.now().minusMinutes(10))){
////                    && !foundSeat.getBooking().getBookingId().equals(
////                            bookSeatDTO.getBookingId()))
//
//                 log.info("Booking ID on seat: {}",
//                         foundSeat.getBooking() != null ? foundSeat.getBooking().getBookingId() : "null");
//                 log.info("Booking ID from request: {}", bookSeatDTO.getBookingId());
//
//                 throw new SeatAlreadyReservedException("Seat is temporarily locked by another user. Try again later");
//            }
            foundSeat.setBooking(foundSeat.getBooking());
            foundSeat.setSeatStatus(SeatStatus.BOOKED);
            foundSeat.setLockTime(null);
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
