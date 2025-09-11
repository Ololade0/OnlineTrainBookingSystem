package train.booking.train.booking.service.Impl;//package OnlineBookingSystem.OnlineBookingSystem.service.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import train.booking.train.booking.dto.BookSeatDTO;
import train.booking.train.booking.dto.GenerateSeatDto;
import train.booking.train.booking.dto.response.BaseResponse;
import train.booking.train.booking.dto.response.ResponseUtil;
import train.booking.train.booking.exceptions.*;
import train.booking.train.booking.model.Booking;
import train.booking.train.booking.model.Seat;
import train.booking.train.booking.model.Train;
import train.booking.train.booking.model.enums.SeatStatus;
import train.booking.train.booking.model.enums.TrainClass;
import train.booking.train.booking.repository.SeatRepository;
import train.booking.train.booking.service.SeatService;
import train.booking.train.booking.service.TrainService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SeatServiceImpl implements SeatService {

private final SeatRepository seatRepository;

private final TrainService trainService;

    @Async
    public BaseResponse  generateSeats(List<GenerateSeatDto> seatDtos, Long trainId) {
        Train foundTrain = trainService.findTrainById(trainId);
        validateSeatGeneration(seatDtos, foundTrain);

        try {
            List<Seat> seatsToSave = new ArrayList<>();

            for (GenerateSeatDto seatDto : seatDtos) {
                for (int i = seatDto.getStartSeat(); i <= seatDto.getEndSeat(); i++) {
                    Seat seat = new Seat();
                    seat.setSeatNumber(i);
                    seat.setSeatStatus(SeatStatus.AVAILABLE);
                    seat.setTrainClass(seatDto.getTrainClass());
                    seat.setTrainId(foundTrain.getId());
                    seatsToSave.add(seat);
                }
            }
            List<Seat> savedSeats = seatRepository.saveAll(seatsToSave);
            List<GenerateSeatDto> seatDtoList = savedSeats.stream().map(seat -> {
                GenerateSeatDto dto = new GenerateSeatDto();
                dto.setSeatNumber(seat.getSeatNumber());
                dto.setStatus(seat.getSeatStatus());
                dto.setTrainClass(seat.getTrainClass());
                return dto;
            }).toList();

            return ResponseUtil.success("Seats successfully generated.", null);

        } catch (Exception e) {
            log.error("Error generating seats: {}", e.getMessage());
            return ResponseUtil.invalidOrNullInput("Error generating seats: " + e.getMessage());
        }
    }
//    public List<Map<String, Object>> getSeatSummary(@PathVariable Long trainId) {
//        List<Seat> seats = seatRepository.findByTrainId(trainId);
//        Map<TrainClass, List<Seat>> grouped = seats.stream()
//                .collect(Collectors.groupingBy(Seat::getTrainClass));
//
//        // Build summary
//        List<Map<String, Object>> summary = new ArrayList<>();
//        grouped.forEach((trainClass, seatList) -> {
//            long available = seatList.stream()
//                    .filter(s -> s.getSeatStatus() == SeatStatus.AVAILABLE)
//                    .count();
//            Map<String, Object> map = new HashMap<>();
//            map.put("trainClass", trainClass);
//            map.put("totalSeats", seatList.size());
//            map.put("availableSeats", available);
//            summary.add(map);
//        });
//
//        return summary;
//
//    }

    public List<Map<String, Object>> getSeatSummary(@PathVariable Long trainId) {
        List<Seat> seats = seatRepository.findByTrainId(trainId);
        Map<TrainClass, List<Seat>> grouped = seats.stream()
                .collect(Collectors.groupingBy(Seat::getTrainClass));

        List<Map<String, Object>> summary = new ArrayList<>();

        for (TrainClass trainClass : TrainClass.values()) {
            List<Seat> seatList = grouped.getOrDefault(trainClass, Collections.emptyList());
            long available = seatList.stream()
                    .filter(s -> s.getSeatStatus() == SeatStatus.AVAILABLE)
                    .count();
            long booked = seatList.stream()
                    .filter(s -> s.getSeatStatus() == SeatStatus.BOOKED)
                    .count();

            long locked = seatList.stream()
                    .filter(s -> s.getSeatStatus() == SeatStatus. TEMPORARILY_LOCKED)
                    .count();
            long unAvailable = seatList.stream()
                    .filter(s -> s.getSeatStatus() == SeatStatus. UNAVAILABLE)
                    .count();
            Map<String, Object> map = new HashMap<>();
            map.put("trainClass", trainClass);
            map.put("totalSeats", seatList.size());
//            map.put("availableSeats", available);
//            map.put("Booked", booked);
//            map.put("locked", locked);
//            map.put("unAvailable", unAvailable);
            summary.add(map);
        }

        return summary;
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
    public void checkSeatAvailability(int seatNumber, Long trainId, TrainClass trainClass) {
    Seat seat = seatRepository.findBySeatNumberAndTrainIdAndTrainClass(seatNumber, trainId, trainClass)
            .orElseThrow(() -> new SeatException("Seat not found for this schedule"));

    if (seat.getSeatStatus() == SeatStatus.BOOKED) {
        throw new SeatAlreadyBookedException("Seat is already booked");
    }

    if (seat.getSeatStatus() == SeatStatus.TEMPORARILY_LOCKED && seat.getLockTime() != null &&
            seat.getLockTime().isAfter(LocalDateTime.now().minusMinutes(10))) {
        throw new SeatAlreadyReservedException("Seat is temporarily reserved. Try again later");
    }

//        if (seat.getBooking().getBookingId() == null || !seat.getBooking().getBookingId().equals(bookSeatDTO.getBookingId())) {
//            throw new BookingCannotBeFoundException("Booking Id cannot be found or doesn't match: " + seat.getBooking().getBookingId());
//        }


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
                new SeatException("Seat with number" + seatNumber + "cannot be found"));
    }

    @Override
    public Optional<Seat> findSeatById(Long seatId) {
        return Optional.ofNullable(seatRepository.findById(seatId).orElseThrow(() ->
                new SeatException("Seat cannot be found")));
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


    private void validateSeatGeneration(List<GenerateSeatDto> seatDtos, Train train) {
        if (train == null) {
            throw new TrainException("Train cannot be found.");
        }

        if (seatDtos == null || seatDtos.isEmpty()) {
            throw new TrainException("Seat generation list cannot be empty.");
        }

        for (GenerateSeatDto seatDto : seatDtos) {
            if (seatDto.getTrainClass() == null) {
                throw new TrainClassException("TrainClass cannot be null.");
            }

            if (!train.getTrainClasses().contains(seatDto.getTrainClass())) {
                throw new TrainException("TrainClass " + seatDto.getTrainClass() + " does not exist for this train.");
            }

            if (seatDto.getStartSeat() <= 0 || seatDto.getEndSeat() <= 0) {
                throw new TrainException("Seat numbers must be greater than zero.");
            }
            if (seatDto.getStartSeat() > seatDto.getEndSeat()) {
                throw new TrainException(
                        String.format("Start seat (%d) cannot be greater than end seat (%d) for class %s.",
                                seatDto.getStartSeat(), seatDto.getEndSeat(), seatDto.getTrainClass()));
            }
            Set<Integer> seatRange = new HashSet<>();
            for (int i = seatDto.getStartSeat(); i <= seatDto.getEndSeat(); i++) {
                if (!seatRange.add(i)) {
                    throw new TrainException(
                            String.format("Duplicate seat number %d found in DTO for class %s.", i, seatDto.getTrainClass()));
                }
            }

            int seatsToCreate = seatDtos.stream()
                    .mapToInt(dto -> dto.getEndSeat() - dto.getStartSeat() + 1)
                    .sum();
            int countedSeat  = seatRepository.countByTrainId(train.getId());

            if (countedSeat + seatsToCreate > train.getTotalSeat()) {
                throw new SeatException("Cannot generate seats. Total seat limit ("
                        + train.getTotalSeat() + ") exceeded.");
            }
            Set<Integer> existingSeats = seatRepository
                    .findSeatNumbersByTrainIdAndTrainClass(train.getId(), seatDto.getTrainClass());

            for (int i = seatDto.getStartSeat(); i <= seatDto.getEndSeat(); i++) {
                if (existingSeats.contains(i)) {
                    throw new TrainException(String.format(
                            "Seat number %d already exists for class %s in  %s.",
                            i, seatDto.getTrainClass(), train.getTrainName()));
                }
            }
        }
    }

}







