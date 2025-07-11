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
import train.booking.train.booking.dto.GenerateSeatDto;
import train.booking.train.booking.dto.response.BaseResponse;
import train.booking.train.booking.dto.response.ResponseUtil;
import train.booking.train.booking.exceptions.*;
import train.booking.train.booking.model.Booking;
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
    public BaseResponse generateSeats(List<GenerateSeatDto> seatDtos, Long scheduleId) {
       Schedule foundSchedule = scheduleService.findSchedulesById(scheduleId);
        if(foundSchedule == null){
            throw new ScheduleCannotBeFoundException("Schedule cannot be found");
        }
        try {
            List<Seat> seats = new ArrayList<>();

            for (GenerateSeatDto seatDto : seatDtos) {
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

            List<GenerateSeatDto> seatDtoList = savedSeats.stream().map(seat -> {
                GenerateSeatDto dto = new GenerateSeatDto();
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



    @Transactional
    public String   lockSeatTemporarilyForPayment(int seatNumber, Long scheduleId, TrainClass trainClass, Booking booking) {
            if (booking == null || booking.getBookingId() == null) {
                throw new IllegalStateException("Booking must be saved before locking the seat.");
            }

            Seat seat = seatRepository.findBySeatNumberAndTrainClass(seatNumber, trainClass);
            if (seat == null) {
                throw new InvalidSeatNumberException("Seat not found");
            }

            if (seat.getSeatStatus() == SeatStatus.BOOKED) {
                throw new SeatAlreadyBookedException("Seat already booked");
            }

            seat.setSeatStatus(SeatStatus.TEMPORARILY_LOCKED);
            seat.setLockTime(LocalDateTime.now());
            seat.setBooking(booking);

            seatRepository.save(seat);
            log.info("Locked seat {} for bookingId={}", seatNumber, booking.getBookingId());
            return "Seat has been locked for 10 mins ";

        }




    @Override
    public void checkSeatAvailability(int seatNumber, Long scheduleId, TrainClass trainClass) {
    Seat seat = seatRepository.findBySeatNumberAndScheduleIdAndTrainClass(seatNumber, scheduleId, trainClass)
            .orElseThrow(() -> new SeatCannotBeFoundException("Seat not found for this schedule"));

    if (seat.getSeatStatus() == SeatStatus.BOOKED) {
        throw new SeatAlreadyBookedException("Seat is already booked");
    }

    if (seat.getSeatStatus() == SeatStatus.TEMPORARILY_LOCKED && seat.getLockTime() != null &&
            seat.getLockTime().isAfter(LocalDateTime.now().minusMinutes(10))) {
        throw new SeatAlreadyReservedException("Seat is temporarily reserved. Try again later");
    }

}
    @Scheduled(fixedRate = 60000)
    public void releaseLockedSeatAfterExpiration() {
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(10);
        List<Seat> expiredSeats = seatRepository.findBySeatStatusAndLockTimeBefore(SeatStatus.TEMPORARILY_LOCKED, threshold);

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

    @Override
    public void updateSeat(Seat bookedSeat) {
        seatRepository.save(bookedSeat);
    }


    @Override
    public Page<Seat> findAllSeat(int page, int size) {
        Page foundSeat = seatRepository.findAll(PageRequest.of(page, size));
        return foundSeat;
    }


@Transactional
public Seat bookSeat(BookSeatDTO bookSeatDTO) {
    try {
        Seat seat = seatRepository.findBySeatNumberAndTrainClass(
                bookSeatDTO.getSeatNumber(), bookSeatDTO.getTrainClass());

        if (seat == null) {
            throw new InvalidSeatNumberException("Seat Number cannot be found");
        }

        if (seat.getSeatStatus() == SeatStatus.BOOKED) {
            throw new SeatAlreadyBookedException("Seat Number : " + seat.getSeatNumber() + " is already booked");
        }

        if (seat.getSeatStatus() == SeatStatus.TEMPORARILY_LOCKED &&
                seat.getLockTime() != null &&
                seat.getLockTime().isAfter(LocalDateTime.now().minusMinutes(10))) {

            if (seat.getBooking().getBookingId() == null || !seat.getBooking().getBookingId().equals(bookSeatDTO.getBookingId())) {
                log.warn("Seat {} is locked by bookingId={}, not matching {}",
                        seat.getSeatNumber(), seat.getBooking().getBookingId(), bookSeatDTO.getBookingId());
                throw new SeatAlreadyReservedException("Seat is temporarily locked by another user.");
            }
        }

        if (seat.getBooking().getBookingId() == null || !seat.getBooking().getBookingId().equals(bookSeatDTO.getBookingId())) {
            throw new BookingCannotBeFoundException("Booking Id cannot be found or doesn't match: " + seat.getBooking().getBookingId());
        }

        seat.setSeatStatus(SeatStatus.BOOKED);
        seat.setLockTime(null);
        return seatRepository.save(seat);

    } catch (Exception e) {
        log.error("Error booking seat", e);
        throw new InvalidSeatNumberException("Error booking Seat");
    }
}





}
